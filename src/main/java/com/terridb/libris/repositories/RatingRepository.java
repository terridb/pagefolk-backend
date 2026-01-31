package com.terridb.libris.repositories;

import com.terridb.libris.entities.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
}
