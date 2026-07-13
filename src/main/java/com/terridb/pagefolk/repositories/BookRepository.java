package com.terridb.pagefolk.repositories;

import com.terridb.pagefolk.entities.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    boolean existsByIsbn(String isbn);

    Page<BookEntity> findAllByAuthors_IdAndDeletedDateIsNull(Long authorId, Pageable pageable);

    @Query("""
        SELECT DISTINCT b FROM BookEntity b
        LEFT JOIN b.authors a
        LEFT JOIN b.genres g
        LEFT JOIN b.series s
        WHERE
            (:search IS NULL OR
             LOWER(b.title) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR
             LOWER(b.isbn) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR
             LOWER(a.fullName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR
             LOWER(s.title) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))
            AND (:#{#authorIds == null ? 1 : 0} = 1 OR a.id IN :authorIds)
            AND (:#{#genres == null ? 1 : 0} = 1 OR LOWER(g.name) IN :genres)
            AND (CAST(:seriesId AS long) IS NULL OR s.id = :seriesId)
            AND (CAST(:releaseAfter AS localdate) IS NULL OR b.releaseDate >= :releaseAfter)
            AND (CAST(:releaseBefore AS localdate) IS NULL OR b.releaseDate <= :releaseBefore)
            AND (CAST(:minRating AS double) IS NULL OR b.averageRating >= :minRating)
            AND b.deletedDate IS NULL
    """)
    Page<BookEntity> searchBooksWithFilters(
            @Param("search") String search,
            @Param("authorIds") List<Long> authorIds,
            @Param("genres") List<String> genres,
            @Param("seriesId") Long seriesId,
            @Param("releaseAfter") LocalDate releaseAfter,
            @Param("releaseBefore") LocalDate releaseBefore,
            @Param("minRating") Double minRating,
            Pageable pageable
    );
}
