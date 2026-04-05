package com.terridb.libris.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "authors")
public class AuthorEntity extends BaseEntity {
    @Column(nullable = false)
    private String fullName;

    private LocalDate deletedAt;

    @ManyToMany(mappedBy = "authors")
    private Set<BookEntity> books = new HashSet<>();

    @ManyToMany(mappedBy = "favoriteAuthors")
    private Set<ProfileEntity> profilesWithFavorite = new HashSet<>();
}
