package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.dtos.user.*;
import com.terridb.pagefolk.entities.RatingEntity;
import com.terridb.pagefolk.entities.UserEntity;
import com.terridb.pagefolk.exceptions.ConflictException;
import com.terridb.pagefolk.exceptions.RecordNotFoundException;
import com.terridb.pagefolk.mappers.UserMapper;
import com.terridb.pagefolk.repositories.RatingRepository;
import com.terridb.pagefolk.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    UserEntity user;
    UserDetailedResponseDto userDetailedDto;
    UserAdminResponseDto userAdminDto;
    UserAdminDetailedResponseDto userAdminDetailedDto;
    JwtAuthenticationToken authentication;
    Jwt jwt;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setKcid("kc-123");
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        userDetailedDto = new UserDetailedResponseDto();
        userDetailedDto.setId("1");

        userAdminDto = new UserAdminResponseDto();
        userAdminDto.setId("1");

        userAdminDetailedDto = new UserAdminDetailedResponseDto();
        userAdminDetailedDto.setId("1");

        jwt = mock(Jwt.class);
        authentication = mock(JwtAuthenticationToken.class);
    }

    private void stubAuthentication() {
        when(jwt.getSubject()).thenReturn("kc-123");
        when(authentication.getToken()).thenReturn(jwt);
    }

    // findOrCreateUser

    @Test
    void findOrCreateUser_existingUser_returnsUser() {
        stubAuthentication();
        when(userRepository.existsByKcid("kc-123")).thenReturn(true);
        when(userRepository.findByKcid("kc-123")).thenReturn(Optional.of(user));
        when(userMapper.toDetailedDto(user)).thenReturn(userDetailedDto);

        UserDetailedResponseDto result = userService.findOrCreateUser(authentication);

        assertEquals("1", result.getId());
        verify(userRepository).findByKcid("kc-123");
    }

    @Test
    void findOrCreateUser_newUser_savesAndReturnsUser() {
        stubAuthentication();
        when(jwt.getClaimAsString("preferred_username")).thenReturn("newuser");
        when(jwt.getClaimAsString("email")).thenReturn("new@example.com");
        when(userRepository.existsByKcid("kc-123")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(userMapper.toDetailedDto(user)).thenReturn(userDetailedDto);

        UserDetailedResponseDto result = userService.findOrCreateUser(authentication);

        verify(userRepository).save(any(UserEntity.class));
        assertEquals("1", result.getId());
    }

    @Test
    void findOrCreateUser_newUser_createsDefaultLists() {
        stubAuthentication();
        when(jwt.getClaimAsString("preferred_username")).thenReturn("newuser");
        when(jwt.getClaimAsString("email")).thenReturn("new@example.com");
        when(userRepository.existsByKcid("kc-123")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(userMapper.toDetailedDto(user)).thenReturn(userDetailedDto);

        userService.findOrCreateUser(authentication);

        verify(userRepository).save(argThat(u -> u.getLists().size() == 4));
    }

    // updateUser

    @Test
    void updateUser_updatesUsername() {
        stubAuthentication();
        UserUpdateRequestDto input = new UserUpdateRequestDto();
        input.setUsername("newname");

        when(userRepository.existsByKcid("kc-123")).thenReturn(true);
        when(userRepository.findByKcid("kc-123")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsernameAndKcidNot("newname", "kc-123")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDetailedDto(user)).thenReturn(userDetailedDto);

        userService.updateUser(authentication, input);

        assertEquals("newname", user.getUsername());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_updatesBio() {
        stubAuthentication();
        UserUpdateRequestDto input = new UserUpdateRequestDto();
        input.setBio("My bio");

        when(userRepository.existsByKcid("kc-123")).thenReturn(true);
        when(userRepository.findByKcid("kc-123")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDetailedDto(user)).thenReturn(userDetailedDto);

        userService.updateUser(authentication, input);

        assertEquals("My bio", user.getBio());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_usernameTaken_throwsConflictException() {
        stubAuthentication();
        UserUpdateRequestDto input = new UserUpdateRequestDto();
        input.setUsername("takenname");

        when(userRepository.existsByKcid("kc-123")).thenReturn(true);
        when(userRepository.findByKcid("kc-123")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsernameAndKcidNot("takenname", "kc-123")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.updateUser(authentication, input));
    }

    // getAllUsers

    @Test
    void getAllUsers_returnsPaginatedResults() {
        Page<UserEntity> page = new PageImpl<>(List.of(user));
        PaginatedResponse<UserSimpleResponseDto> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setContent(List.of(new UserSimpleResponseDto()));

        when(userRepository.searchUsersWithParams(any(), eq(false), any(Pageable.class))).thenReturn(page);
        when(userMapper.toPaginatedSimpleDto(page)).thenReturn(paginatedResponse);

        PaginatedResponse<UserSimpleResponseDto> result = userService.getAllUsers(new UserFilterDto());

        assertEquals(1, result.getContent().size());
    }

    // getAllUsersForAdmins

    @Test
    void getAllUsersForAdmins_returnsPaginatedResults() {
        Page<UserEntity> page = new PageImpl<>(List.of(user));
        PaginatedResponse<UserAdminResponseDto> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setContent(List.of(userAdminDto));

        when(userRepository.searchUsersWithParams(any(), any(), any(Pageable.class))).thenReturn(page);
        when(userMapper.toPaginatedDto(page)).thenReturn(paginatedResponse);

        PaginatedResponse<UserAdminResponseDto> result = userService.getAllUsersForAdmins(null, null, 0, 20, "username", "ASC");

        assertEquals(1, result.getContent().size());
    }

    // softDeleteCurrentUser

    @Test
    void softDeleteCurrentUser_setsDeletedDate() {
        stubAuthentication();
        when(userRepository.findByKcid("kc-123")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.softDeleteCurrentUser(authentication);

        assertNotNull(user.getDeletedDate());
        verify(userRepository).save(user);
    }

    @Test
    void softDeleteCurrentUser_userNotFound_throwsNotFoundException() {
        stubAuthentication();
        when(userRepository.findByKcid("kc-123")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userService.softDeleteCurrentUser(authentication));
    }

    @Test
    void softDeleteCurrentUser_alreadyDeleted_throwsConflictException() {
        stubAuthentication();
        user.setDeletedDate(LocalDate.now());
        when(userRepository.findByKcid("kc-123")).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.softDeleteCurrentUser(authentication));
    }

    // softDeleteUserById

    @Test
    void softDeleteUserById_setsDeletedDate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.softDeleteUserById(1L);

        assertNotNull(user.getDeletedDate());
        verify(userRepository).save(user);
    }

    @Test
    void softDeleteUserById_notFound_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userService.softDeleteUserById(99L));
    }

    @Test
    void softDeleteUserById_alreadyDeleted_throwsConflictException() {
        user.setDeletedDate(LocalDate.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.softDeleteUserById(1L));
    }

    // reviveUserById

    @Test
    void reviveUserById_clearsDeletedDate() {
        user.setDeletedDate(LocalDate.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.reviveUserById(1L);

        assertNull(user.getDeletedDate());
        verify(userRepository).save(user);
    }

    @Test
    void reviveUserById_notFound_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userService.reviveUserById(99L));
    }

    @Test
    void reviveUserById_notDeleted_throwsConflictException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.reviveUserById(1L));
    }

    // deleteUserById

    @Test
    void deleteUserById_deletesUser_andNullsRatings() {
        user.setDeletedDate(LocalDate.now());
        RatingEntity rating = new RatingEntity();
        rating.setUser(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ratingRepository.findAllByUser(user)).thenReturn(List.of(rating));

        userService.deleteUserById(1L);

        assertNull(rating.getUser());
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserById_notFound_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userService.deleteUserById(99L));
    }

    @Test
    void deleteUserById_notSoftDeleted_throwsConflictException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.deleteUserById(1L));
    }

    // getUserById

    @Test
    void getUserById_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDetailedDto(user)).thenReturn(userDetailedDto);

        UserDetailedResponseDto result = userService.getUserById(1L);

        assertEquals("1", result.getId());
    }

    @Test
    void getUserById_notFound_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void getUserById_softDeleted_throwsNotFoundException() {
        user.setDeletedDate(LocalDate.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(RecordNotFoundException.class, () -> userService.getUserById(1L));
    }

    // getUserByIdForAdmins

    @Test
    void getUserByIdForAdmins_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toAdminDetailedDto(user)).thenReturn(userAdminDetailedDto);

        UserAdminDetailedResponseDto result = userService.getUserByIdForAdmins(1L);

        assertEquals("1", result.getId());
    }

    @Test
    void getUserByIdForAdmins_notFound_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userService.getUserByIdForAdmins(99L));
    }
}
