package com.terridb.pagefolk.repositories;

import com.terridb.pagefolk.entities.AuthorEntity;
import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.BookSeriesEntity;
import com.terridb.pagefolk.entities.GenreEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {"spring.sql.init.mode=never"})
class BookRepositoryIT {

    @Autowired BookRepository bookRepository;
    @Autowired AuthorRepository authorRepository;
    @Autowired GenreRepository genreRepository;
    @Autowired BookSeriesRepository bookSeriesRepository;

    Pageable pageable = PageRequest.of(0, 10);

    AuthorEntity tolkien;
    AuthorEntity rowling;
    GenreEntity fantasy;
    GenreEntity romance;
    BookSeriesEntity lotrSeries;
    BookEntity fellowship;
    BookEntity harryPotter;
    BookEntity standaloneBook;
    BookEntity deletedBook;

    @BeforeEach
    void setUp() {
        tolkien = new AuthorEntity();
        tolkien.setFullName("J.R.R. Tolkien");
        tolkien = authorRepository.save(tolkien);

        rowling = new AuthorEntity();
        rowling.setFullName("J.K. Rowling");
        rowling = authorRepository.save(rowling);

        fantasy = new GenreEntity();
        fantasy.setName("fantasy");
        fantasy = genreRepository.save(fantasy);

        romance = new GenreEntity();
        romance.setName("romance");
        romance = genreRepository.save(romance);

        lotrSeries = new BookSeriesEntity();
        lotrSeries.setTitle("Lord of the Rings");
        lotrSeries = bookSeriesRepository.save(lotrSeries);

        fellowship = new BookEntity();
        fellowship.setTitle("The Fellowship of the Ring");
        fellowship.setIsbn("9780261102354");
        fellowship.setReleaseDate(LocalDate.of(1954, 7, 29));
        fellowship.setAverageRating(BigDecimal.valueOf(4.8));
        fellowship.setSeries(lotrSeries);
        fellowship.getAuthors().add(tolkien);
        fellowship.getGenres().add(fantasy);
        fellowship = bookRepository.save(fellowship);

        harryPotter = new BookEntity();
        harryPotter.setTitle("Harry Potter and the Philosopher's Stone");
        harryPotter.setIsbn("9780747532699");
        harryPotter.setReleaseDate(LocalDate.of(1997, 6, 26));
        harryPotter.setAverageRating(BigDecimal.valueOf(4.5));
        harryPotter.getAuthors().add(rowling);
        harryPotter.getGenres().add(fantasy);
        harryPotter = bookRepository.save(harryPotter);

        standaloneBook = new BookEntity();
        standaloneBook.setTitle("A Romance Novel");
        standaloneBook.setIsbn("9780000000001");
        standaloneBook.setReleaseDate(LocalDate.of(2020, 1, 1));
        standaloneBook.setAverageRating(BigDecimal.valueOf(3.5));
        standaloneBook.getGenres().add(romance);
        standaloneBook = bookRepository.save(standaloneBook);

        deletedBook = new BookEntity();
        deletedBook.setTitle("Deleted Book");
        deletedBook.setIsbn("9780000000002");
        deletedBook.setDeletedDate(LocalDate.now());
        deletedBook = bookRepository.save(deletedBook);
    }

    @Test
    void searchBooks_noFilters_returnsAllNonDeleted() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters(null, null, null, null, null, null, null, pageable);

        assertEquals(3, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(b -> b.getDeletedDate() == null));
    }

    @Test
    void searchBooks_byTitle_returnsMatch() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters("Fellowship", null, null, null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(fellowship.getId(), result.getContent().getFirst().getId());
    }

    @Test
    void searchBooks_byIsbn_returnsMatch() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters("9780747532699", null, null, null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(harryPotter.getId(), result.getContent().getFirst().getId());
    }

    @Test
    void searchBooks_byAuthorName_returnsMatch() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters("Tolkien", null, null, null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(fellowship.getId(), result.getContent().getFirst().getId());
    }

    @Test
    void searchBooks_bySeriesTitle_returnsMatch() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters("Lord of the Rings", null, null, null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(fellowship.getId(), result.getContent().getFirst().getId());
    }

    @Test
    void searchBooks_searchCaseInsensitive() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters("fellowship", null, null, null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchBooks_byAuthorIds_returnsMatch() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters(null, List.of(tolkien.getId()), null, null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(fellowship.getId(), result.getContent().getFirst().getId());
    }

    @Test
    void searchBooks_byGenres_returnsMatch() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters(null, null, List.of("romance"), null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(standaloneBook.getId(), result.getContent().getFirst().getId());
    }

    @Test
    void searchBooks_byGenres_multipleGenres_returnsMatches() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters(null, null, List.of("fantasy", "romance"), null, null, null, null, pageable);

        assertEquals(3, result.getTotalElements());
    }

    @Test
    void searchBooks_bySeriesId_returnsMatch() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters(null, null, null, lotrSeries.getId(), null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(fellowship.getId(), result.getContent().getFirst().getId());
    }

    @Test
    void searchBooks_byReleaseAfter_returnsMatch() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters(null, null, null, null, LocalDate.of(1990, 1, 1), null, null, pageable);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void searchBooks_byReleaseBefore_returnsMatch() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters(null, null, null, null, null, LocalDate.of(1960, 1, 1), null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(fellowship.getId(), result.getContent().getFirst().getId());
    }

    @Test
    void searchBooks_byMinRating_returnsMatch() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters(null, null, null, null, null, null, 4.6, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(fellowship.getId(), result.getContent().getFirst().getId());
    }

    @Test
    void searchBooks_excludesDeleted() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters("Deleted", null, null, null, null, null, null, pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    void searchBooks_noMatch_returnsEmpty() {
        Page<BookEntity> result = bookRepository.searchBooksWithFilters("zzznomatch", null, null, null, null, null, null, pageable);

        assertEquals(0, result.getTotalElements());
    }
}
