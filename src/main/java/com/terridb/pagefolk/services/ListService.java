package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.list.ListRequestDto;
import com.terridb.pagefolk.dtos.list.ListResponseDto;
import com.terridb.pagefolk.dtos.list.ListUpdateDto;
import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.ListEntity;
import com.terridb.pagefolk.exceptions.ConflictException;
import com.terridb.pagefolk.exceptions.RecordNotFoundException;
import com.terridb.pagefolk.mappers.ListMapper;
import com.terridb.pagefolk.repositories.BookRepository;
import com.terridb.pagefolk.repositories.ListRepository;
import com.terridb.pagefolk.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListService {

    private final ListRepository listRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ListMapper listMapper;

    public ListService(ListRepository listRepository, BookRepository bookRepository, UserRepository userRepository, ListMapper listMapper) {
        this.listRepository = listRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.listMapper = listMapper;
    }

    public List<ListResponseDto> getListsByUserId(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RecordNotFoundException("User with id " + userId + " not found"));
        if (user.getDeletedDate() != null) {
            throw new RecordNotFoundException("User with id " + userId + " not found");
        }
        return listMapper.toDto(listRepository.findAllByUserId(userId));
    }

    public ListResponseDto getListById(Long id) {
        return listMapper.toDto(
                listRepository.findById(id)
                        .orElseThrow(() -> new RecordNotFoundException("List with id " + id + " not found")));
    }

    public ListResponseDto postList(ListRequestDto input) {
        ListEntity entity = listMapper.toEntity(input, null);
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

    public ListResponseDto addBookToList(Long listId, Long bookId) {
        ListEntity list = listRepository.findById(listId)
                .orElseThrow(() -> new RecordNotFoundException("List with id " + listId + " not found"));

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book with id " + bookId + " not found"));

        if (book.getDeletedDate() != null) {
            throw new RecordNotFoundException("Book with id " + bookId + " not found");
        }

        if (list.getBooks().contains(book)) {
            throw new ConflictException("Book with id " + bookId + " is already in this list");
        }

        list.getBooks().add(book);
        return listMapper.toDto(listRepository.save(list));
    }

    public void removeBookFromList(Long listId, Long bookId) {
        ListEntity list = listRepository.findById(listId)
                .orElseThrow(() -> new RecordNotFoundException("List with id " + listId + " not found"));

        BookEntity book = list.getBooks().stream()
                .filter(b -> b.getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Book with id " + bookId + " not found in this list"));

        list.getBooks().remove(book);
        listRepository.save(list);
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
