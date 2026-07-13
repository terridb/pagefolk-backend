package com.terridb.pagefolk.controllers;

import com.terridb.pagefolk.dtos.genre.GenreRequestDto;
import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.GenreEntity;
import com.terridb.pagefolk.repositories.BookRepository;
import com.terridb.pagefolk.repositories.GenreRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GenreControllerIT {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    GenreRepository genreRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    JwtDecoder jwtDecoder;

    GenreEntity fantasy;
    BookEntity book;

    @BeforeEach
    void setUp() {
        fantasy = new GenreEntity();
        fantasy.setName("fantasy");
        fantasy = genreRepository.save(fantasy);

        book = new BookEntity();
        book.setTitle("Fourth Wing");
        book.setIsbn("2222222222222");
        book.getGenres().add(fantasy);
        book = bookRepository.save(book);
    }

    @AfterEach
    void tearDown() {
        if (bookRepository.existsById(book.getId())) bookRepository.deleteById(book.getId());
    }

    @Test
    void getAllGenres_returns200WithGenres() throws Exception {
        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("fantasy"));
    }

    @Test
    void getGenreById_returns200() throws Exception {
        mockMvc.perform(get("/genres/" + fantasy.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("fantasy"));
    }

    @Test
    void getGenreById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/genres/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBooksByGenreId_returns200WithContent() throws Exception {
        mockMvc.perform(get("/genres/" + fantasy.getId() + "/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Fourth Wing"));
    }

    @Test
    void getBooksByGenreId_notFound_returns404() throws Exception {
        mockMvc.perform(get("/genres/99999/books"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postGenre_asAdmin_returns201AndPersists() throws Exception {
        GenreRequestDto input = new GenreRequestDto();
        input.setName("horror");

        mockMvc.perform(post("/genres")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("horror"));
    }

    @Test
    void postGenre_noRole_returns403() throws Exception {
        GenreRequestDto input = new GenreRequestDto();
        input.setName("horror");

        mockMvc.perform(post("/genres")
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isForbidden());
    }

    @Test
    void postGenre_blankName_returns400() throws Exception {
        GenreRequestDto input = new GenreRequestDto();
        input.setName("");

        mockMvc.perform(post("/genres")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postGenre_duplicateName_returns409() throws Exception {
        GenreRequestDto input = new GenreRequestDto();
        input.setName("fantasy");

        mockMvc.perform(post("/genres")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateGenre_asAdmin_returns200AndUpdates() throws Exception {
        GenreRequestDto input = new GenreRequestDto();
        input.setName("dark fantasy");

        mockMvc.perform(patch("/genres/" + fantasy.getId())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("dark fantasy"));
    }

    @Test
    void updateGenre_noRole_returns403() throws Exception {
        GenreRequestDto input = new GenreRequestDto();
        input.setName("dark fantasy");

        mockMvc.perform(patch("/genres/" + fantasy.getId())
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateGenre_notFound_returns404() throws Exception {
        GenreRequestDto input = new GenreRequestDto();
        input.setName("dark fantasy");

        mockMvc.perform(patch("/genres/99999")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteGenre_asAdmin_returns204() throws Exception {
        mockMvc.perform(delete("/genres/" + fantasy.getId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteGenre_noRole_returns403() throws Exception {
        mockMvc.perform(delete("/genres/" + fantasy.getId())
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteGenre_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/genres/99999")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }
}
