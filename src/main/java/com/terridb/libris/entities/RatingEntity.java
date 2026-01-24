package com.terridb.libris.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ratings")
public class RatingEntity extends BaseEntity {
    @Column(nullable = false)
    private int score;

    private String review;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookEntity book;

//    user
}
