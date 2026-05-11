package com.terridb.pagefolk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lists")
public class ListEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;
    private boolean required = false;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    @ManyToMany
    @JoinTable(
            name = "list_books",
            joinColumns = @JoinColumn(name = "list_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<BookEntity> books = new HashSet<>();
}
