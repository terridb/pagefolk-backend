package com.terridb.pagefolk.repositories;

import com.terridb.pagefolk.entities.AuthorEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {"spring.sql.init.mode=never"})
class AuthorRepositoryIT {

    @Autowired
    AuthorRepository authorRepository;

    Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        AuthorEntity tolkien = new AuthorEntity();
        tolkien.setFullName("J.R.R. Tolkien");

        AuthorEntity rowling = new AuthorEntity();
        rowling.setFullName("J.K. Rowling");

        AuthorEntity deleted = new AuthorEntity();
        deleted.setFullName("Deleted Author");
        deleted.setDeletedDate(LocalDate.now());

        authorRepository.save(tolkien);
        authorRepository.save(rowling);
        authorRepository.save(deleted);
    }

    @Test
    void searchAuthors_noSearch_returnsAllNonDeleted() {
        Page<AuthorEntity> result = authorRepository.searchAuthorsWithParams(null, pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(a -> a.getDeletedDate() == null));
    }

    @Test
    void searchAuthors_byFullName_returnsMatch() {
        Page<AuthorEntity> result = authorRepository.searchAuthorsWithParams("Tolkien", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("J.R.R. Tolkien", result.getContent().getFirst().getFullName());
    }

    @Test
    void searchAuthors_partialMatch() {
        Page<AuthorEntity> result = authorRepository.searchAuthorsWithParams("J.", pageable);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void searchAuthors_caseInsensitive() {
        Page<AuthorEntity> result = authorRepository.searchAuthorsWithParams("tolkien", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchAuthors_noMatch_returnsEmpty() {
        Page<AuthorEntity> result = authorRepository.searchAuthorsWithParams("zzznomatch", pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    void searchAuthors_excludesDeleted() {
        Page<AuthorEntity> result = authorRepository.searchAuthorsWithParams("Deleted", pageable);

        assertEquals(0, result.getTotalElements());
    }
}
