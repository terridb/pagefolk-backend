package com.terridb.libris.services;

import com.terridb.libris.dtos.genre.GenreRequestDto;
import com.terridb.libris.dtos.genre.GenreResponseDto;
import com.terridb.libris.entities.GenreEntity;
import com.terridb.libris.exceptions.ConflictException;
import com.terridb.libris.exceptions.RecordNotFoundException;
import com.terridb.libris.mappers.GenreMapper;
import com.terridb.libris.repositories.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public GenreService(GenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
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

    public GenreResponseDto postGenre(GenreRequestDto input) {
        if (!genreRepository.findAllByNameEqualsIgnoreCase(input.getName()).isEmpty()) {
            throw new ConflictException("Genre with name " + input.getName() + " already exists");
        }

        GenreEntity entity = genreMapper.toEntity(input, null);
        return genreMapper.toDto(genreRepository.save(entity));
    }
}
