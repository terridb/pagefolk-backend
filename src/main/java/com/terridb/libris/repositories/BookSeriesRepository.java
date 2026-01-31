package com.terridb.libris.repositories;

import com.terridb.libris.entities.BookSeriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookSeriesRepository extends JpaRepository<BookSeriesEntity, Long> {
}
