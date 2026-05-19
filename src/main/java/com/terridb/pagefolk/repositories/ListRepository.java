package com.terridb.pagefolk.repositories;

import com.terridb.pagefolk.entities.ListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListRepository extends JpaRepository<ListEntity, Long> {
    List<ListEntity> findAllByUserId(Long userId);
}
