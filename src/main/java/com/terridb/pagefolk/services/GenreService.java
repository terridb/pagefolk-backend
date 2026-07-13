package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.book.BookResponseDto;
import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.dtos.genre.GenreRequestDto;
import com.terridb.pagefolk.dtos.genre.GenreResponseDto;
import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.GenreEntity;
import com.terridb.pagefolk.exceptions.ConflictException;
import com.terridb.pagefolk.exceptions.RecordNotFoundException;
import com.terridb.pagefolk.mappers.BookMapper;
import com.terridb.pagefolk.mappers.GenreMapper;
import com.terridb.pagefolk.repositories.BookRepository;
import com.terridb.pagefolk.repositories.GenreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public GenreService(GenreRepository genreRepository, GenreMapper genreMapper, BookRepository bookRepository, BookMapper bookMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public List<GenreResponseDto> getAllGenres() {
        List<GenreEntity> entities = genreRepository.findAll();
        return genreMapper.toDto(entities);
    }

    public GenreResponseDto getGenreById(Long id) {
        return genreMapper.toDto(
                genreRepository.findById(id)
                        .orElseThrow(() -> new RecordNotFoundException("Genre with id " + id + " not found")));
    }

    public PaginatedResponse<BookResponseDto> getBooksByGenreId(Long genreId, int page, int size) {
        if (!genreRepository.existsById(genreId)) {
            throw new RecordNotFoundException("Genre with id " + genreId + " not found");
        }

        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;

        Pageable pageable = PageRequest.of(page, size);
        Page<BookEntity> booksPage = bookRepository.findAllByGenres_IdAndDeletedDateIsNull(genreId, pageable);
        return bookMapper.toPaginatedDto(booksPage);
    }

    public GenreResponseDto postGenre(GenreRequestDto input) {
        if (!genreRepository.findAllByNameEqualsIgnoreCase(input.getName()).isEmpty()) {
            throw new ConflictException("Genre with name " + input.getName() + " already exists");
        }

        GenreEntity entity = genreMapper.toEntity(input, null);
        return genreMapper.toDto(genreRepository.save(entity));
    }

    public GenreResponseDto updateGenre(Long genreId, GenreRequestDto input) {
        GenreEntity existingEntity = genreRepository.findById(genreId)
                .orElseThrow(() -> new RecordNotFoundException("Genre with id " + genreId + " not found"));

        if (input.getName() != null && !input.getName().trim().isEmpty()) {
            existingEntity.setName(input.getName().trim().toLowerCase());
        }

        if (input.getDescription() != null && !input.getDescription().trim().isEmpty()) {
            existingEntity.setDescription(input.getDescription().trim());
        }

        GenreEntity savedEntity = genreRepository.save(existingEntity);
        return genreMapper.toDto(savedEntity);
    }

    public void deleteGenre(Long genreId) {
        GenreEntity existingEntity = genreRepository.findById(genreId)
                .orElseThrow(() -> new RecordNotFoundException("Genre with id " + genreId + " not found"));

        existingEntity.getBooks().forEach(book -> book.getGenres().remove(existingEntity));

        genreRepository.deleteById(genreId);
    }
}
