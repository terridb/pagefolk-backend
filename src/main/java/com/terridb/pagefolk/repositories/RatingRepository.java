package com.terridb.pagefolk.repositories;

import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.RatingEntity;
import com.terridb.pagefolk.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
    List<RatingEntity> findAllByUser(UserEntity user);
    Page<RatingEntity> findAllByUser(UserEntity user, Pageable pageable);
    Page<RatingEntity> findAllByBook(BookEntity book, Pageable pageable);
}
