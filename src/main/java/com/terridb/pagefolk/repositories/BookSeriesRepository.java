package com.terridb.pagefolk.repositories;

import com.terridb.pagefolk.entities.BookSeriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookSeriesRepository extends JpaRepository<BookSeriesEntity, Long> {
}
