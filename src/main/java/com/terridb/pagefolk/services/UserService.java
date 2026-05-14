package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.dtos.user.UserAdminDetailedResponseDto;
import com.terridb.pagefolk.dtos.user.UserAdminResponseDto;
import com.terridb.pagefolk.dtos.user.UserDetailedResponseDto;
import com.terridb.pagefolk.dtos.user.UserFilterDto;
import com.terridb.pagefolk.dtos.user.UserSimpleResponseDto;
import com.terridb.pagefolk.entities.ListEntity;
import com.terridb.pagefolk.entities.UserEntity;
import com.terridb.pagefolk.exceptions.RecordNotFoundException;
import com.terridb.pagefolk.mappers.UserMapper;
import com.terridb.pagefolk.repositories.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
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

    public PaginatedResponse<UserSimpleResponseDto> getAllUsers(UserFilterDto filter) {
        if (filter.getPage() < 0) filter.setPage(0);
        if (filter.getSize() <= 0 || filter.getSize() > 100) filter.setSize(20);

        Sort.Direction sortDirection = Sort.Direction.fromString(filter.getDirection());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by(sortDirection, filter.getSortBy()));

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
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        return userMapper.toPaginatedDto(userRepository.searchUsersWithParams(search, deleted, pageable));
    }

    public UserAdminDetailedResponseDto getUserByIdForAdmins(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User with id " + id + " not found"));
        return userMapper.toAdminDetailedDto(user);
    }

//    Get user by id with public DTO is needed by everyone when authenticated

//    Search users is needed by everyone when authenticated
}
