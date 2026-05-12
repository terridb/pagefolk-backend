package com.terridb.pagefolk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private UUID id;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "edited_date")
    private LocalDateTime editDate;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    private boolean deleted = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ProfileEntity profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ListEntity> lists;

    @OneToMany(mappedBy = "user")
    private List<RatingEntity> ratings;

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
