package com.terridb.pagefolk.repositories;

import com.terridb.pagefolk.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {"spring.sql.init.mode=never"})
class UserRepositoryIT {

    @Autowired
    UserRepository userRepository;

    Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        UserEntity alice = new UserEntity();
        alice.setKcid("kc-1");
        alice.setUsername("alice");
        alice.setEmail("alice@example.com");

        UserEntity bob = new UserEntity();
        bob.setKcid("kc-2");
        bob.setUsername("bob");
        bob.setEmail("bob@test.com");

        UserEntity deleted = new UserEntity();
        deleted.setKcid("kc-3");
        deleted.setUsername("charlie");
        deleted.setEmail("charlie@example.com");
        deleted.setDeletedDate(LocalDate.now());

        userRepository.save(alice);
        userRepository.save(bob);
        userRepository.save(deleted);
    }

    @Test
    void searchUsers_noFilter_returnsOnlyNonDeleted() {
        Page<UserEntity> result = userRepository.searchUsersWithParams(null, false, pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(u -> u.getDeletedDate() == null));
    }

    @Test
    void searchUsers_searchByUsername_returnsMatch() {
        Page<UserEntity> result = userRepository.searchUsersWithParams("alice", false, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("alice", result.getContent().getFirst().getUsername());
    }

    @Test
    void searchUsers_searchByEmail_returnsMatch() {
        Page<UserEntity> result = userRepository.searchUsersWithParams("@test.com", false, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("bob", result.getContent().getFirst().getUsername());
    }

    @Test
    void searchUsers_searchCaseInsensitive() {
        Page<UserEntity> result = userRepository.searchUsersWithParams("ALICE", false, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchUsers_noMatch_returnsEmpty() {
        Page<UserEntity> result = userRepository.searchUsersWithParams("zzznomatch", false, pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    void searchUsers_deletedTrue_returnsOnlyDeleted() {
        Page<UserEntity> result = userRepository.searchUsersWithParams(null, true, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("charlie", result.getContent().getFirst().getUsername());
    }

    @Test
    void searchUsers_deletedNull_returnsAll() {
        Page<UserEntity> result = userRepository.searchUsersWithParams(null, null, pageable);

        assertEquals(3, result.getTotalElements());
    }
}
