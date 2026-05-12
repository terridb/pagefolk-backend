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
@Table(name = "profiles")
public class ProfileEntity extends BaseEntity {
    @Column(length = 1024)
    private String profilePictureUrl;

    @Column(length = 500)
    private String bio;

    @ManyToMany
    @JoinTable(
            name = "profile_favorite_books",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<BookEntity> favoriteBooks = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "profile_favorite_authors",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<AuthorEntity> favoriteAuthors = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

}
