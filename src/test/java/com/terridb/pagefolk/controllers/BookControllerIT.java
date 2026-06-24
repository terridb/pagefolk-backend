package com.terridb.pagefolk.controllers;

import com.terridb.pagefolk.dtos.book.BookPatchDto;
import com.terridb.pagefolk.dtos.book.BookRequestDto;
import com.terridb.pagefolk.entities.AuthorEntity;
import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.GenreEntity;
import com.terridb.pagefolk.repositories.AuthorRepository;
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

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired BookRepository bookRepository;
    @Autowired AuthorRepository authorRepository;
    @Autowired GenreRepository genreRepository;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean JwtDecoder jwtDecoder;

    AuthorEntity emily;
    GenreEntity romance;
    BookEntity book;
    BookEntity deletedBook;

    @BeforeEach
    void setUp() {
        emily = new AuthorEntity();
        emily.setFullName("Emily Henry");
        emily = authorRepository.save(emily);

        romance = new GenreEntity();
        romance.setName("romance");
        romance = genreRepository.save(romance);

        book = new BookEntity();
        book.setTitle("Beach Read");
        book.setIsbn("1111111111111");
        book.getAuthors().add(emily);
        book.getGenres().add(romance);
        book = bookRepository.save(book);

        deletedBook = new BookEntity();
        deletedBook.setTitle("Deleted Book");
        deletedBook.setIsbn("2222222222222");
        deletedBook.getAuthors().add(emily);
        deletedBook.getGenres().add(romance);
        deletedBook.setDeletedDate(LocalDate.now());
        deletedBook = bookRepository.save(deletedBook);
    }

    @AfterEach
    void tearDown() {
        if (bookRepository.existsById(book.getId())) bookRepository.deleteById(book.getId());
        if (bookRepository.existsById(deletedBook.getId())) bookRepository.deleteById(deletedBook.getId());
    }

    @Test
    void getAllBooks_returns200WithContent() throws Exception {
        mockMvc.perform(get("/books").param("search", "Beach Read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Beach Read"));
    }

    @Test
    void getBookById_returns200() throws Exception {
        mockMvc.perform(get("/books/" + book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Beach Read"))
                .andExpect(jsonPath("$.isbn").value("1111111111111"));
    }

    @Test
    void getBookById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/books/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postBook_asAdmin_returns201() throws Exception {
        BookRequestDto input = new BookRequestDto();
        input.setTitle("People We Meet on Vacation");
        input.setIsbn("3333333333333");
        input.setAuthorIds(Set.of(emily.getId()));
        input.setGenreIds(Set.of(romance.getId()));

        mockMvc.perform(post("/books")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("People We Meet on Vacation"));
    }

    @Test
    void postBook_blankTitle_returns400() throws Exception {
        BookRequestDto input = new BookRequestDto();
        input.setTitle("");
        input.setIsbn("4444444444444");
        input.setAuthorIds(Set.of(emily.getId()));
        input.setGenreIds(Set.of(romance.getId()));

        mockMvc.perform(post("/books")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postBook_duplicateIsbn_returns409() throws Exception {
        BookRequestDto input = new BookRequestDto();
        input.setTitle("Some Other Book");
        input.setIsbn(book.getIsbn());
        input.setAuthorIds(Set.of(emily.getId()));
        input.setGenreIds(Set.of(romance.getId()));

        mockMvc.perform(post("/books")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isConflict());
    }

    @Test
    void postBook_unknownAuthor_returns404() throws Exception {
        BookRequestDto input = new BookRequestDto();
        input.setTitle("Some Book");
        input.setIsbn("5555555555555");
        input.setAuthorIds(Set.of(99999L));
        input.setGenreIds(Set.of(romance.getId()));

        mockMvc.perform(post("/books")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    void postBook_unknownGenre_returns404() throws Exception {
        BookRequestDto input = new BookRequestDto();
        input.setTitle("Some Book");
        input.setIsbn("6666666666666");
        input.setAuthorIds(Set.of(emily.getId()));
        input.setGenreIds(Set.of(99999L));

        mockMvc.perform(post("/books")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBook_asAdmin_returns200() throws Exception {
        BookPatchDto input = new BookPatchDto();
        input.setTitle("Beach Read Updated");

        mockMvc.perform(patch("/books/" + book.getId())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Beach Read Updated"));
    }

    @Test
    void updateBook_duplicateIsbn_returns409() throws Exception {
        BookPatchDto input = new BookPatchDto();
        input.setIsbn(deletedBook.getIsbn());

        mockMvc.perform(patch("/books/" + book.getId())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateBook_notFound_returns404() throws Exception {
        BookPatchDto input = new BookPatchDto();
        input.setTitle("Beach Read Updated");

        mockMvc.perform(patch("/books/99999")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    void softDeleteBook_asAdmin_returns204() throws Exception {
        mockMvc.perform(patch("/books/" + book.getId() + "/delete")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void softDeleteBook_alreadyDeleted_returns409() throws Exception {
        mockMvc.perform(patch("/books/" + deletedBook.getId() + "/delete")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isConflict());
    }

    @Test
    void softDeleteBook_notFound_returns404() throws Exception {
        mockMvc.perform(patch("/books/99999/delete")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void reviveBook_asAdmin_returns200() throws Exception {
        mockMvc.perform(patch("/books/" + deletedBook.getId() + "/revive")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Deleted Book"));
    }

    @Test
    void reviveBook_notDeleted_returns409() throws Exception {
        mockMvc.perform(patch("/books/" + book.getId() + "/revive")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isConflict());
    }

    @Test
    void reviveBook_notFound_returns404() throws Exception {
        mockMvc.perform(patch("/books/99999/revive")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_asAdmin_returns204() throws Exception {
        mockMvc.perform(delete("/books/" + deletedBook.getId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_notSoftDeleted_returns409() throws Exception {
        mockMvc.perform(delete("/books/" + book.getId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteBook_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/books/99999")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }
}
