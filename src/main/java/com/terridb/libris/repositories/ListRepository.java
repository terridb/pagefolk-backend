package com.terridb.libris.repositories;

import com.terridb.libris.entities.ListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListRepository extends JpaRepository<ListEntity, Long> {
}
