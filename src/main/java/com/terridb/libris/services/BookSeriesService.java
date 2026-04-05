package com.terridb.libris.services;

import com.terridb.libris.dtos.bookseries.BookSeriesResponseDto;
import com.terridb.libris.dtos.common.PaginatedResponse;
import com.terridb.libris.mappers.SeriesMapper;
import com.terridb.libris.repositories.BookSeriesRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookSeriesService {

    private final BookSeriesRepository bookSeriesRepository;
    private final SeriesMapper seriesMapper;

    public BookSeriesService(BookSeriesRepository bookSeriesRepository, SeriesMapper seriesMapper) {
        this.bookSeriesRepository = bookSeriesRepository;
        this.seriesMapper = seriesMapper;
    }

    public PaginatedResponse<BookSeriesResponseDto> getAllBookSeries(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;

        Pageable pageable = PageRequest.of(page, size);
        return seriesMapper.toPaginatedDto(bookSeriesRepository.findAll(pageable));
    }
}
