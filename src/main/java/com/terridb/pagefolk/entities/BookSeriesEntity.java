package com.terridb.pagefolk.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "book_series")
public class BookSeriesEntity extends BaseEntity {
    @Column(nullable = false)
    private String title;

    private String description;

    @OneToMany(mappedBy = "series")
    private Set<BookEntity> books = new HashSet<>();
}
