package com.terridb.libris.services;

import com.terridb.libris.dtos.bookseries.BookSeriesRequestDto;
import com.terridb.libris.dtos.bookseries.BookSeriesResponseDto;
import com.terridb.libris.dtos.common.PaginatedResponse;
import com.terridb.libris.entities.BookEntity;
import com.terridb.libris.entities.BookSeriesEntity;
import com.terridb.libris.exceptions.ConflictException;
import com.terridb.libris.exceptions.RecordNotFoundException;
import com.terridb.libris.mappers.SeriesMapper;
import com.terridb.libris.repositories.BookRepository;
import com.terridb.libris.repositories.BookSeriesRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class BookSeriesService {

    private final BookSeriesRepository bookSeriesRepository;
    private final BookRepository bookRepository;
    private final SeriesMapper seriesMapper;

    public BookSeriesService(BookSeriesRepository bookSeriesRepository, BookRepository bookRepository, SeriesMapper seriesMapper) {
        this.bookSeriesRepository = bookSeriesRepository;
        this.bookRepository = bookRepository;
        this.seriesMapper = seriesMapper;
    }

    public PaginatedResponse<BookSeriesResponseDto> getAllBookSeries(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;

        Pageable pageable = PageRequest.of(page, size);
        return seriesMapper.toPaginatedDto(bookSeriesRepository.findAll(pageable));
    }

    public BookSeriesResponseDto getBookSeriesById(Long id) {
        return seriesMapper.toDto(
                bookSeriesRepository.findById(id)
                        .orElseThrow(() -> new RecordNotFoundException("Book series with id " + id + " not found")));
    }

    public BookSeriesResponseDto postBookSeries(BookSeriesRequestDto input) {
        BookSeriesEntity entity = seriesMapper.toEntity(input, null);
        BookSeriesEntity savedEntity = bookSeriesRepository.save(entity);

//        Set books
        if (input.getBookIds() != null) {
            for (Long bookId : input.getBookIds()) {
                assignBookToSeries(bookId, savedEntity);
            }
        }

        return seriesMapper.toDto(savedEntity);
    }

    public BookSeriesResponseDto updateBookSeries(Long id, BookSeriesRequestDto input) {
        BookSeriesEntity existingEntity = bookSeriesRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Book series with id " + id + " not found"));

        if (input.getTitle() != null) {
            existingEntity.setTitle(input.getTitle().trim());
        }

        if (input.getDescription() != null) {
            existingEntity.setDescription(input.getDescription().trim());
        }

//        Set books
        if (input.getBookIds() != null) {
            Set<Long> currentBookIds = new HashSet<>();

//            Add all books that are already in the series to a Set
            for (BookEntity book : existingEntity.getBooks()) {
                currentBookIds.add(book.getId());
            }

//            Remove books that are not in the input list anymore
            for (BookEntity book : existingEntity.getBooks()) {
                if (!input.getBookIds().contains(book.getId())) {
                    book.setSeries(null);
                    bookRepository.save(book);
                }
            }
            existingEntity.getBooks().removeIf(book -> !input.getBookIds().contains(book.getId()));

//            Add new books that are in the input list but not in the current series
            for (Long bookId : input.getBookIds()) {
                if (!currentBookIds.contains(bookId)) {
                    assignBookToSeries(bookId, existingEntity);
                }
            }
        }

        BookSeriesEntity savedEntity = bookSeriesRepository.save(existingEntity);
        return seriesMapper.toDto(savedEntity);
    }

    public void deleteBookSeries(Long id) {
        BookSeriesEntity existingEntity = bookSeriesRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Book series with id " + id + " not found"));

        existingEntity.getBooks().forEach(book -> book.setSeries(null));

        bookSeriesRepository.delete(existingEntity);
    }

    private void assignBookToSeries(Long bookId, BookSeriesEntity series) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book with id " + bookId + " not found"));
        if (book.getSeries() != null) {
            throw new ConflictException("Book with id " + bookId + " already belongs to a series");
        }
        book.setSeries(series);
        bookRepository.save(book);
        series.getBooks().add(book);
    }
}
