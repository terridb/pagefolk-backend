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

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

}
