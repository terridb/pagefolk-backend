package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.book.BookSimpleResponseDto;
import com.terridb.pagefolk.dtos.list.ListRequestDto;
import com.terridb.pagefolk.dtos.list.ListResponseDto;
import com.terridb.pagefolk.dtos.list.ListUpdateDto;
import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.ListEntity;
import com.terridb.pagefolk.entities.UserEntity;
import com.terridb.pagefolk.exceptions.ConflictException;
import com.terridb.pagefolk.exceptions.RecordNotFoundException;
import com.terridb.pagefolk.mappers.ListMapper;
import com.terridb.pagefolk.repositories.BookRepository;
import com.terridb.pagefolk.repositories.ListRepository;
import com.terridb.pagefolk.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListServiceTest {

    @Mock
    private ListRepository listRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ListMapper listMapper;

    @InjectMocks
    private ListService listService;

    ListEntity list;
    ListResponseDto listDto;
    UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);

        list = new ListEntity();
        list.setId(1L);
        list.setName("My List");

        listDto = new ListResponseDto();
        listDto.setId(1L);
        listDto.setName("My List");
    }

    // getListsByUserId

    @Test
    void getListsByUserId_returnsLists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(listRepository.findAllByUserId(1L)).thenReturn(List.of(list));
        when(listMapper.toDto(List.of(list))).thenReturn(List.of(listDto));

        List<ListResponseDto> result = listService.getListsByUserId(1L);

        assertEquals(1, result.size());
        assertEquals("My List", result.getFirst().getName());
    }

    @Test
    void getListsByUserId_userNotFound_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> listService.getListsByUserId(99L));
    }

    @Test
    void getListsByUserId_deletedUser_throwsNotFoundException() {
        user.setDeletedDate(LocalDate.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(RecordNotFoundException.class, () -> listService.getListsByUserId(1L));
    }

    // getListById

    @Test
    void getListById_returnsList() {
        when(listRepository.findById(1L)).thenReturn(Optional.of(list));
        when(listMapper.toDto(list)).thenReturn(listDto);

        ListResponseDto result = listService.getListById(1L);

        assertEquals("My List", result.getName());
    }

    @Test
    void getListById_throwsNotFoundException() {
        when(listRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> listService.getListById(99L));
    }

    // postList

    @Test
    void postList_savesList() {
        ListRequestDto input = new ListRequestDto();

        when(listMapper.toEntity(input, null)).thenReturn(list);
        when(listRepository.save(list)).thenReturn(list);
        when(listMapper.toDto(list)).thenReturn(listDto);

        ListResponseDto result = listService.postList(input);

        verify(listRepository).save(list);
        assertEquals("My List", result.getName());
    }

    // updateList

    @Test
    void updateList_throwsNotFoundException() {
        when(listRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> listService.updateList(99L, new ListUpdateDto()));
    }

    @Test
    void updateList_withName_updatesName() {
        ListUpdateDto input = new ListUpdateDto();
        input.setName("Updated Name");

        when(listRepository.findById(1L)).thenReturn(Optional.of(list));
        when(listRepository.save(list)).thenReturn(list);
        when(listMapper.toDto(list)).thenReturn(listDto);

        listService.updateList(1L, input);

        verify(listRepository).save(list);
        assertEquals("Updated Name", list.getName());
    }

    @Test
    void updateList_withNullName_doesNotUpdateName() {
        ListUpdateDto input = new ListUpdateDto();

        when(listRepository.findById(1L)).thenReturn(Optional.of(list));
        when(listRepository.save(list)).thenReturn(list);
        when(listMapper.toDto(list)).thenReturn(listDto);

        listService.updateList(1L, input);

        verify(listRepository).save(list);
        assertEquals("My List", list.getName());
    }

    @Test
    void updateList_withDescription_updatesDescription() {
        ListUpdateDto input = new ListUpdateDto();
        input.setDescription("A great reading list");

        when(listRepository.findById(1L)).thenReturn(Optional.of(list));
        when(listRepository.save(list)).thenReturn(list);
        when(listMapper.toDto(list)).thenReturn(listDto);

        listService.updateList(1L, input);

        verify(listRepository).save(list);
        assertEquals("A great reading list", list.getDescription());
    }

    @Test
    void updateList_withNullDescription_doesNotUpdateDescription() {
        ListUpdateDto input = new ListUpdateDto();

        when(listRepository.findById(1L)).thenReturn(Optional.of(list));
        when(listRepository.save(list)).thenReturn(list);
        when(listMapper.toDto(list)).thenReturn(listDto);

        listService.updateList(1L, input);

        verify(listRepository).save(list);
        assertNull(list.getDescription());
    }

    // addBookToList

    @Test
    void addBookToList_SavesBookToList() {
        BookEntity book =  new BookEntity();
        book.setId(1L);
        BookSimpleResponseDto bookDto = new  BookSimpleResponseDto();
        bookDto.setId(1L);
        listDto.setBooks(List.of(bookDto));

        when(listRepository.findById(1L)).thenReturn(Optional.of(list));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(listRepository.save(list)).thenReturn(list);
        when(listMapper.toDto(list)).thenReturn(listDto);

        ListResponseDto result = listService.addBookToList(1L, 1L);

        verify(listRepository).save(list);
        assertTrue(result.getBooks().contains(bookDto));
    }

    @Test
    void addBookToList_listNotFound_throwsNotFoundException() {
        when(listRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> listService.addBookToList(99L, 1L));
    }

    @Test
    void addBookToList_bookNotFound_throwsNotFoundException() {
        when(listRepository.findById(1L)).thenReturn(Optional.of(list));
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> listService.addBookToList(1L, 99L));
    }

    @Test
    void addBookToList_deletedBook_throwsNotFoundException() {
        BookEntity book = new BookEntity();
        book.setId(2L);
        book.setDeletedDate(LocalDate.now());

        when(listRepository.findById(1L)).thenReturn(Optional.of(list));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));

        assertThrows(RecordNotFoundException.class, () -> listService.addBookToList(1L, 2L));
    }

    @Test
    void addBookToList_bookAlreadyInList_throwsConflictException() {
        BookEntity book = new BookEntity();
        book.setId(2L);
        list.getBooks().add(book);

        when(listRepository.findById(1L)).thenReturn(Optional.of(list));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));

        assertThrows(ConflictException.class, () -> listService.addBookToList(1L, 2L));
    }

    // removeBookFromList

    @Test
    void removeBookFromList_RemovesBookFromList() {
        BookEntity book =  new BookEntity();
        book.setId(1L);
        list.getBooks().add(book);

        when(listRepository.findById(1L)).thenReturn(Optional.of(list));
        when(listRepository.save(list)).thenReturn(list);

        listService.removeBookFromList(1L, 1L);

        verify(listRepository).save(list);
        assertFalse(list.getBooks().contains(book));
    }

    @Test
    void removeBookFromList_listNotFound_throwsNotFoundException() {
        when(listRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> listService.removeBookFromList(99L, 1L));
    }

    @Test
    void removeBookFromList_bookNotInList_throwsNotFoundException() {
        when(listRepository.findById(1L)).thenReturn(Optional.of(list));

        assertThrows(RecordNotFoundException.class, () -> listService.removeBookFromList(1L, 99L));
    }

    // deleteList

    @Test
    void deleteList_throwsNotFoundException() {
        when(listRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> listService.deleteList(99L));
    }

    @Test
    void deleteList_requiredList_throwsConflictException() {
        list.setRequired(true);
        when(listRepository.findById(1L)).thenReturn(Optional.of(list));

        assertThrows(ConflictException.class, () -> listService.deleteList(1L));
    }

    @Test
    void deleteList_deletesEntity() {
        when(listRepository.findById(1L)).thenReturn(Optional.of(list));

        listService.deleteList(1L);

        verify(listRepository).delete(list);
    }
}
