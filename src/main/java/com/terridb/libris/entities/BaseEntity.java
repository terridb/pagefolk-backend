package com.terridb.libris.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "edited_date")
    private LocalDateTime editDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        editDate = createdDate;
    }

    @PreUpdate
    protected void onUpdate() {
        editDate = LocalDateTime.now();
    }

}
