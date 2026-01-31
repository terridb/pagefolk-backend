package com.terridb.libris.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "profiles")
public class ProfileEntity extends BaseEntity {
    private String imageUrl;
    private boolean deleted = false;

    @OneToMany(mappedBy = "profiles")
    private Set<RatingEntity> ratings;

    @ManyToMany
    @JoinTable(
            name = "profile_favorite_books",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<BookEntity> favoriteBooks;

    @ManyToMany
    @JoinTable(
            name = "profile_favorite_authors",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<AuthorEntity> favoriteAuthors;

    @OneToMany(mappedBy = "profile")
    private Set<ListEntity> lists;

//    todo readingGoals
//    todo stats

}
