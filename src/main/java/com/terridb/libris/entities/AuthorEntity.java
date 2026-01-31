package com.terridb.libris.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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
@Table(name = "authors")
public class AuthorEntity extends BaseEntity {

    private String fullName;

    @ManyToMany(mappedBy = "authors")
    private Set<BookEntity> books = new HashSet<>();
}
