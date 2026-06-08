package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.genre.GenreRequestDto;
import com.terridb.pagefolk.dtos.genre.GenreResponseDto;
import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.GenreEntity;
import com.terridb.pagefolk.exceptions.ConflictException;
import com.terridb.pagefolk.exceptions.RecordNotFoundException;
import com.terridb.pagefolk.mappers.GenreMapper;
import com.terridb.pagefolk.repositories.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;
    @Mock
    private GenreMapper genreMapper;

    @InjectMocks
    private GenreService genreService;

    GenreEntity genre;
    GenreResponseDto genreDto;

    @BeforeEach
    void setUp() {
        genre = new GenreEntity();
        genre.setId(1L);
        genre.setName("fantasy");

        genreDto = new GenreResponseDto();
        genreDto.setId(1L);
        genreDto.setName("fantasy");
    }

    @Test
    void getAllGenres_returnsAllGenres() {
        when(genreRepository.findAll()).thenReturn(List.of(genre));
        when(genreMapper.toDto(List.of(genre))).thenReturn(List.of(genreDto));

        List<GenreResponseDto> result = genreService.getAllGenres();

        assertEquals(1, result.size());
        assertEquals("fantasy", result.getFirst().getName());
    }

    @Test
    void getGenreById_returnsGenre() {
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreMapper.toDto(genre)).thenReturn(genreDto);

        GenreResponseDto result = genreService.getGenreById(1L);

        assertEquals("fantasy", result.getName());
    }

    @Test
    void getGenreById_throwsNotFoundException() {
        when(genreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> genreService.getGenreById(99L));
    }

    @Test
    void postGenre_savesGenre() {
        GenreRequestDto input = new GenreRequestDto();
        input.setName("fantasy");

        when(genreRepository.findAllByNameEqualsIgnoreCase("fantasy")).thenReturn(List.of());
        when(genreMapper.toEntity(input, null)).thenReturn(genre);
        when(genreRepository.save(genre)).thenReturn(genre);
        when(genreMapper.toDto(genre)).thenReturn(genreDto);

        GenreResponseDto result = genreService.postGenre(input);

        verify(genreRepository).save(genre);
        assertEquals("fantasy", result.getName());
    }

    @Test
    void postGenre_throwsConflictException() {
        GenreRequestDto input = new GenreRequestDto();
        input.setName("fantasy");

        when(genreRepository.findAllByNameEqualsIgnoreCase("fantasy")).thenReturn(List.of(genre));

        assertThrows(ConflictException.class, () -> genreService.postGenre(input));
    }

    @Test
    void updateGenre_throwsNotFoundException() {
        when(genreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> genreService.updateGenre(99L, new GenreRequestDto()));
    }

    @Test
    void updateGenre_withName_updatesName() {
        GenreRequestDto input = new GenreRequestDto();
        input.setName("Sci-Fi");

        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreRepository.save(genre)).thenReturn(genre);
        when(genreMapper.toDto(genre)).thenReturn(genreDto);

        genreService.updateGenre(1L, input);

        verify(genreRepository).save(genre);
        assertEquals("sci-fi", genre.getName());
    }

    @Test
    void updateGenre_withBlankName_doesNotUpdateName() {
        GenreRequestDto input = new GenreRequestDto();
        input.setName("   ");

        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreRepository.save(genre)).thenReturn(genre);
        when(genreMapper.toDto(genre)).thenReturn(genreDto);

        genreService.updateGenre(1L, input);

        verify(genreRepository).save(genre);
        assertEquals("fantasy", genre.getName());
    }

    @Test
    void updateGenre_withDescription_updatesDescription() {
        GenreRequestDto input = new GenreRequestDto();
        input.setDescription("Books with magic and dragons");

        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreRepository.save(genre)).thenReturn(genre);
        when(genreMapper.toDto(genre)).thenReturn(genreDto);

        genreService.updateGenre(1L, input);

        verify(genreRepository).save(genre);
        assertEquals("Books with magic and dragons", genre.getDescription());
    }

    @Test
    void updateGenre_withBlankDescription_doesNotUpdateDescription() {
        GenreRequestDto input = new GenreRequestDto();
        input.setDescription("   ");

        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreRepository.save(genre)).thenReturn(genre);
        when(genreMapper.toDto(genre)).thenReturn(genreDto);

        genreService.updateGenre(1L, input);

        verify(genreRepository).save(genre);
        assertNull(genre.getDescription());
    }

    @Test
    void deleteGenre_throwsNotFoundException() {
        when(genreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> genreService.deleteGenre(99L));
    }

    @Test
    void deleteGenre_removesGenreFromBooks() {
        BookEntity book = new BookEntity();
        book.setId(1L);
        book.getGenres().add(genre);
        genre.getBooks().add(book);

        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));

        genreService.deleteGenre(1L);

        verify(genreRepository).deleteById(1L);
        assertFalse(book.getGenres().contains(genre));
    }
}
