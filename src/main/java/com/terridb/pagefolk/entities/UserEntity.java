package com.terridb.pagefolk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity{
   @Column(nullable = false, unique = true)
    private String kcid;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    private LocalDate deletedDate;

    @Column(length = 1024)
    private String profilePictureUrl;

    @Column(length = 500)
    private String bio;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ListEntity> lists = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<RatingEntity> ratings;
}
