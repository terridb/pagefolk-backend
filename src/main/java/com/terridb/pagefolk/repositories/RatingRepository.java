package com.terridb.pagefolk.repositories;

import com.terridb.pagefolk.entities.RatingEntity;
import com.terridb.pagefolk.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
    List<RatingEntity> findAllByUser(UserEntity user);
}
