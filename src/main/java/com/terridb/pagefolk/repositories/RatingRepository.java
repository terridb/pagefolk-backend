package com.terridb.pagefolk.repositories;

import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.RatingEntity;
import com.terridb.pagefolk.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
    List<RatingEntity> findAllByUser(UserEntity user);
    Page<RatingEntity> findAllByUser(UserEntity user, Pageable pageable);
    Page<RatingEntity> findAllByBook(BookEntity book, Pageable pageable);

    @Query("SELECT AVG(r.score) FROM RatingEntity r WHERE r.book = :book")
    BigDecimal calculateAverageRatingByBook(@Param("book") BookEntity book);

    long countByBook(BookEntity book);
}
