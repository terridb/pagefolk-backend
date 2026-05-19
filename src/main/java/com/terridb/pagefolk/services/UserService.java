package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.dtos.user.UserAdminDetailedResponseDto;
import com.terridb.pagefolk.dtos.user.UserAdminResponseDto;
import com.terridb.pagefolk.dtos.user.UserDetailedResponseDto;
import com.terridb.pagefolk.dtos.user.UserFilterDto;
import com.terridb.pagefolk.dtos.user.UserSimpleResponseDto;
import com.terridb.pagefolk.dtos.user.UserUpdateRequestDto;
import com.terridb.pagefolk.exceptions.ConflictException;
import com.terridb.pagefolk.entities.ListEntity;
import com.terridb.pagefolk.entities.UserEntity;
import com.terridb.pagefolk.exceptions.RecordNotFoundException;
import com.terridb.pagefolk.mappers.UserMapper;
import com.terridb.pagefolk.repositories.RatingRepository;
import com.terridb.pagefolk.repositories.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.Stream;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, RatingRepository ratingRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.userMapper = userMapper;
    }

    public UserDetailedResponseDto findOrCreateUser(Authentication authentication) {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        return userMapper.toDetailedDto(findOrCreateUserEntity(jwt));
    }

    private UserEntity findOrCreateUserEntity(Jwt jwt) {
        String kcid = jwt.getSubject();
        if (userRepository.existsByKcid(kcid)) {
            return userRepository.findByKcid(kcid).get();
        } else {
            return createUserEntity(jwt);
        }
    }

    private UserEntity createUserEntity(Jwt jwt) {
        UserEntity userEntity = new UserEntity();
        userEntity.setKcid(jwt.getSubject());
        userEntity.setUsername(jwt.getClaimAsString("preferred_username"));
        userEntity.setEmail(jwt.getClaimAsString("email"));

        Stream.of("Want to Read", "Currently Reading", "Finished", "Did Not Finish")
                .map(name -> createRequiredLists(name, userEntity))
                .forEach(userEntity.getLists()::add);

        return userRepository.save(userEntity);
    }

    private ListEntity createRequiredLists(String name, UserEntity user) {
        ListEntity list = new ListEntity();
        list.setName(name);
        list.setRequired(true);
        list.setUser(user);
        return list;
    }

    public UserDetailedResponseDto updateUser(Authentication authentication, UserUpdateRequestDto input) {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        UserEntity user = findOrCreateUserEntity(jwt);

        if (input.getUsername() != null) {
            if (userRepository.existsByUsernameAndKcidNot(input.getUsername(), user.getKcid())) {
                throw new ConflictException("Username " + input.getUsername() + " is already taken");
            }
            user.setUsername(input.getUsername());
        }
        if (input.getBio() != null) {
            user.setBio(input.getBio());
        }

        return userMapper.toDetailedDto(userRepository.save(user));
    }

    public PaginatedResponse<UserSimpleResponseDto> getAllUsers(UserFilterDto filter) {
        Pageable pageable = buildPageable(filter.getPage(), filter.getSize(), filter.getSortBy(), filter.getDirection());
        return userMapper.toPaginatedSimpleDto(userRepository.searchUsersWithParams(filter.getSearch(), false, pageable));
    }

    public PaginatedResponse<UserAdminResponseDto> getAllUsersForAdmins(
            String search,
            Boolean deleted,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        return userMapper.toPaginatedDto(userRepository.searchUsersWithParams(search, deleted, pageable));
    }

    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;
        return PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
    }

    public void softDeleteCurrentUser(Authentication authentication) {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        UserEntity user = userRepository.findByKcid(jwt.getSubject())
                .orElseThrow(() -> new RecordNotFoundException("User not found"));

        if (user.getDeletedDate() != null) {
            throw new ConflictException("User is already soft-deleted");
        }

        user.setDeletedDate(LocalDate.now());
        userRepository.save(user);
    }

    public void softDeleteUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User with id " + id + " not found"));

        if (user.getDeletedDate() != null) {
            throw new ConflictException("User with id " + id + " is already soft-deleted");
        }

        user.setDeletedDate(LocalDate.now());
        userRepository.save(user);
    }

    public void reviveUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User with id " + id + " not found"));

        if (user.getDeletedDate() == null) {
            throw new ConflictException("User with id " + id + " is not soft-deleted and cannot be revived");
        }

        user.setDeletedDate(null);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User with id " + id + " not found"));

        if (user.getDeletedDate() == null) {
            throw new ConflictException("User with id " + id + " should first be soft-deleted before full delete");
        }

        ratingRepository.findAllByUser(user).forEach(r -> r.setUser(null));
        userRepository.delete(user);
    }

    public UserDetailedResponseDto getUserById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User with id " + id + " not found"));
        if (user.getDeletedDate() != null) {
            throw new RecordNotFoundException("User with id " + id + " not found");
        }
        return userMapper.toDetailedDto(user);
    }

    public UserAdminDetailedResponseDto getUserByIdForAdmins(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User with id " + id + " not found"));
        return userMapper.toAdminDetailedDto(user);
    }
}
