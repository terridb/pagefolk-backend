package com.terridb.pagefolk.mappers;

import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.dtos.user.UserAdminResponseDto;
import com.terridb.pagefolk.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public UserAdminResponseDto toDto(UserEntity entity) {
        var result = new UserAdminResponseDto();
        result.setId(entity.getId().toString());
        result.setCreatedDate(entity.getCreatedDate() != null ? entity.getCreatedDate().toString() : null);
        result.setEditedDate(entity.getEditDate() != null ? entity.getEditDate().toString() : null);
        result.setUsername(entity.getUsername());
        result.setEmail(entity.getEmail());
        result.setDeletedDate(entity.getDeletedDate() != null ? entity.getDeletedDate().toString() : null);
        result.setProfileId(entity.getProfile() != null ? entity.getProfile().getId() : null);
        return result;
    }

    public List<UserAdminResponseDto> toDto(List<UserEntity> entities) {
        var result = new ArrayList<UserAdminResponseDto>();
        for (UserEntity entity : entities) {
            result.add(toDto(entity));
        }
        return result;
    }

    public PaginatedResponse<UserAdminResponseDto> toPaginatedDto(Page<UserEntity> entitiesPage) {
        PaginatedResponse<UserAdminResponseDto> response = new PaginatedResponse<>();
        response.setContent(toDto(entitiesPage.getContent()));
        response.setPage(entitiesPage.getNumber());
        response.setSize(entitiesPage.getSize());
        response.setTotalElements(entitiesPage.getTotalElements());
        response.setTotalPages(entitiesPage.getTotalPages());
        response.setHasNextPage(response.getPage() + 1 < response.getTotalPages());
        response.setHasPreviousPage(response.getPage() > 0);
        return response;
    }
}
