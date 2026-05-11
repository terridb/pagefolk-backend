package com.terridb.pagefolk.repositories;

import com.terridb.pagefolk.entities.ListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListRepository extends JpaRepository<ListEntity, Long> {
}
