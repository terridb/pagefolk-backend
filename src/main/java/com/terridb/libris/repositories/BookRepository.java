package com.terridb.libris.repositories;

import com.terridb.libris.entities.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    boolean existsByIsbn(String isbn);

    Page<BookEntity> findDistinctByTitleContainingIgnoreCaseOrIsbnContainingIgnoreCaseOrAuthors_FullNameContainingIgnoreCaseOrSeries_TitleContainingIgnoreCase(
            String title,
            String isbn,
            String authors,
            String series,
            Pageable pageable
    );

    default Page<BookEntity> searchBooks(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return findAll(pageable);
        }
        String term = search.trim();
        return findDistinctByTitleContainingIgnoreCaseOrIsbnContainingIgnoreCaseOrAuthors_FullNameContainingIgnoreCaseOrSeries_TitleContainingIgnoreCase(
                term, term, term, term, pageable
        );
    }
}
