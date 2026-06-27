package com.terridb.pagefolk.controllers;

import com.terridb.pagefolk.dtos.author.AuthorRequestDto;
import com.terridb.pagefolk.entities.AuthorEntity;
import com.terridb.pagefolk.repositories.AuthorRepository;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorControllerIT {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AuthorRepository authorRepository;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    JwtDecoder jwtDecoder;

    AuthorEntity emily;
    AuthorEntity deletedEmily;

    @BeforeEach
    void setUp() {
        emily = new AuthorEntity();
        emily.setFullName("Emily Henry");
        emily = authorRepository.save(emily);

        deletedEmily = new AuthorEntity();
        deletedEmily.setFullName("Deleted Author");
        deletedEmily.setDeletedDate(LocalDate.now());
        deletedEmily = authorRepository.save(deletedEmily);
    }

    @Test
    void getAllAuthors_returns200WithContent() throws Exception {
        mockMvc.perform(get("/authors").param("search", "Emily Henry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("Emily Henry"));
    }

    @Test
    void getAuthorById_returns200() throws Exception {
        mockMvc.perform(get("/authors/" + emily.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Emily Henry"));
    }

    @Test
    void getAuthorById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/authors/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postAuthor_asAdmin_returns201() throws Exception {
        AuthorRequestDto input = new AuthorRequestDto();
        input.setFullName("Stephen King");

        mockMvc.perform(post("/authors")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Stephen King"));
    }

    @Test
    void postAuthor_blankName_returns400() throws Exception {
        AuthorRequestDto input = new AuthorRequestDto();
        input.setFullName("");

        mockMvc.perform(post("/authors")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAuthor_asAdmin_returns200() throws Exception {
        AuthorRequestDto input = new AuthorRequestDto();
        input.setFullName("Emily Henry Updated");

        mockMvc.perform(put("/authors/" + emily.getId())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Emily Henry Updated"));
    }

    @Test
    void updateAuthor_notFound_returns404() throws Exception {
        AuthorRequestDto input = new AuthorRequestDto();
        input.setFullName("Emily Henry Updated");

        mockMvc.perform(put("/authors/99999")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    void softDeleteAuthor_asAdmin_returns204() throws Exception {
        mockMvc.perform(patch("/authors/" + emily.getId() + "/delete")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void softDeleteAuthor_alreadyDeleted_returns409() throws Exception {
        mockMvc.perform(patch("/authors/" + deletedEmily.getId() + "/delete")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isConflict());
    }

    @Test
    void softDeleteAuthor_notFound_returns404() throws Exception {
        mockMvc.perform(patch("/authors/99999/delete")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void reviveAuthor_asAdmin_returns200() throws Exception {
        mockMvc.perform(patch("/authors/" + deletedEmily.getId() + "/revive")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Deleted Author"));
    }

    @Test
    void reviveAuthor_notDeleted_returns409() throws Exception {
        mockMvc.perform(patch("/authors/" + emily.getId() + "/revive")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isConflict());
    }

    @Test
    void reviveAuthor_notFound_returns404() throws Exception {
        mockMvc.perform(patch("/authors/99999/revive")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAuthor_asAdmin_returns204() throws Exception {
        mockMvc.perform(delete("/authors/" + deletedEmily.getId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAuthor_notSoftDeleted_returns409() throws Exception {
        mockMvc.perform(delete("/authors/" + emily.getId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteAuthor_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/authors/99999")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }
}
