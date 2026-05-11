package com.terridb.pagefolk.repositories;

import com.terridb.pagefolk.entities.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
}
