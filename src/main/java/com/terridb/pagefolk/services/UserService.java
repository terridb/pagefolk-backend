package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.dtos.user.UserAdminResponseDto;
import com.terridb.pagefolk.mappers.UserMapper;
import com.terridb.pagefolk.repositories.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    //    Get all users is only needed by admins and moderators
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

//    Get user by id with detailed admin DTO is only needed by admins and moderators

//    Get user by id with public DTO is needed by everyone when authenticated

//    Search users is needed by everyone when authenticated
}
