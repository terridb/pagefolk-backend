package com.terridb.pagefolk.repositories;

import com.terridb.pagefolk.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Query("""
                SELECT u FROM UserEntity u
                WHERE
                    (:search IS NULL
                     OR LOWER(u.username) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                     OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))
                    AND (CAST(:deleted AS boolean) IS NULL
                         OR (:deleted = true AND u.deletedDate IS NOT NULL)
                         OR (:deleted = false AND u.deletedDate IS NULL))
            """)
    Page<UserEntity> searchUsersWithParams(
            @Param("search") String search,
            @Param("deleted") Boolean deleted,
            Pageable pageable
    );
}
