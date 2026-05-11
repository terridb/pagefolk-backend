package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.genre.GenreRequestDto;
import com.terridb.pagefolk.dtos.genre.GenreResponseDto;
import com.terridb.pagefolk.entities.GenreEntity;
import com.terridb.pagefolk.exceptions.ConflictException;
import com.terridb.pagefolk.exceptions.RecordNotFoundException;
import com.terridb.pagefolk.mappers.GenreMapper;
import com.terridb.pagefolk.repositories.GenreRepository;
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
