package com.terridb.pagefolk.controllers;

import com.terridb.pagefolk.dtos.list.ListRequestDto;
import com.terridb.pagefolk.dtos.list.ListResponseDto;
import com.terridb.pagefolk.dtos.list.ListUpdateDto;
import com.terridb.pagefolk.services.ListService;
import com.terridb.pagefolk.utils.UrlUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/lists")
public class ListController {

    private final ListService listService;

    public ListController(ListService listService) {
        this.listService = listService;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ListResponseDto>> getListsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(listService.getListsByUserId(userId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ListResponseDto> getListById(@PathVariable Long id) {
        return ResponseEntity.ok(listService.getListById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ListResponseDto> postList(@Valid @RequestBody ListRequestDto input) {
        ListResponseDto dto = listService.postList(input);
        return ResponseEntity.created(UrlUtil.generateUri("lists", dto.getId())).body(dto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ListResponseDto> updateList(@Valid @RequestBody ListUpdateDto input, @PathVariable Long id) {
        return ResponseEntity.ok(listService.updateList(id, input));
    }

    @PostMapping("/{listId}/books/{bookId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ListResponseDto> addBookToList(@PathVariable Long listId, @PathVariable Long bookId) {
        return ResponseEntity.ok(listService.addBookToList(listId, bookId));
    }

    @DeleteMapping("/{listId}/books/{bookId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> removeBookFromList(@PathVariable Long listId, @PathVariable Long bookId) {
        listService.removeBookFromList(listId, bookId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteList(@PathVariable Long id) {
        listService.deleteList(id);
        return ResponseEntity.noContent().build();
    }
}
