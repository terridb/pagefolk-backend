package com.terridb.libris.repositories;

import com.terridb.libris.entities.AuthorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {
    @Query("""
                SELECT DISTINCT a FROM AuthorEntity a
                WHERE
                    (:search IS NULL OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))
                            AND a.deletedAt IS NULL
            """)
    Page<AuthorEntity> searchAuthorsWithParams(
            @Param("search") String search,
            Pageable pageable
    );

}
