package com.terridb.pagefolk.repositories;

import com.terridb.pagefolk.entities.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreRepository extends JpaRepository<GenreEntity, Long> {

   List<GenreEntity> findAllByNameEqualsIgnoreCase(String name);
}
