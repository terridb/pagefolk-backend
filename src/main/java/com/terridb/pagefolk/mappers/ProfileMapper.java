package com.terridb.pagefolk.mappers;

import com.terridb.pagefolk.dtos.profile.ProfileRequestDto;
import com.terridb.pagefolk.dtos.profile.ProfileResponseDto;
import com.terridb.pagefolk.entities.ProfileEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProfileMapper {
    public ProfileEntity toEntity(ProfileRequestDto requestDto, ProfileEntity profile) {
        if (profile == null) {
            profile = new ProfileEntity();
        }

        return profile;
    }

    public ProfileResponseDto toDto(ProfileEntity entity) {
        var result = new ProfileResponseDto();
        result.setId(entity.getId());
        result.setProfilePictureUrl(entity.getProfilePictureUrl());
        result.setBio(entity.getBio());

        return result;
    }

    public List<ProfileResponseDto> toDto(List<ProfileEntity> entities) {
        var result = new ArrayList<ProfileResponseDto>();
        for (ProfileEntity entity : entities) {
            result.add(toDto(entity));
        }
        return result;
    }
}
