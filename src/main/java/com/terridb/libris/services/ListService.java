package com.terridb.libris.services;

import com.terridb.libris.dtos.list.ListRequestDto;
import com.terridb.libris.dtos.list.ListResponseDto;
import com.terridb.libris.dtos.list.ListUpdateDto;
import com.terridb.libris.entities.ListEntity;
// import com.terridb.libris.entities.ProfileEntity;
import com.terridb.libris.exceptions.ConflictException;
import com.terridb.libris.exceptions.RecordNotFoundException;
import com.terridb.libris.mappers.ListMapper;
import com.terridb.libris.repositories.ListRepository;
// import com.terridb.libris.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class ListService {

    private final ListRepository listRepository;
//    private final ProfileRepository profileRepository;
    private final ListMapper listMapper;

    public ListService(ListRepository listRepository, /*ProfileRepository profileRepository,*/ ListMapper listMapper) {
        this.listRepository = listRepository;
//        this.profileRepository = profileRepository;
        this.listMapper = listMapper;
    }

    public ListResponseDto getListById(Long id) {
        return listMapper.toDto(
                listRepository.findById(id)
                        .orElseThrow(() -> new RecordNotFoundException("List with id " + id + " not found")));
    }

    public ListResponseDto postList(ListRequestDto input) {
//        ProfileEntity profile = profileRepository.findById(input.getProfileId())
//                .orElseThrow(() -> new RecordNotFoundException("Profile with id " + input.getProfileId() + " not found"));

        ListEntity entity = listMapper.toEntity(input, null);
//        entity.setProfile(profile);

        return listMapper.toDto(listRepository.save(entity));
    }

    public ListResponseDto updateList(Long id, ListUpdateDto input) {
        ListEntity entity = listRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("List with id " + id + " not found"));

        if (input.getName() != null) {
            entity.setName(input.getName().trim());
        }
        if (input.getDescription() != null) {
            entity.setDescription(input.getDescription().trim());
        }

        return listMapper.toDto(listRepository.save(entity));
    }

    public void deleteList(Long id) {
        ListEntity entity = listRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("List with id " + id + " not found"));

        if (entity.isRequired()) {
            throw new ConflictException("List with id " + id + " is required and cannot be deleted");
        }

        listRepository.delete(entity);
    }
}
