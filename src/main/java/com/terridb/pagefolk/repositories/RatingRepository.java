package com.terridb.pagefolk.repositories;

import com.terridb.pagefolk.entities.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
}
