package com.terridb.libris.controllers;

import com.terridb.libris.dtos.list.ListRequestDto;
import com.terridb.libris.dtos.list.ListResponseDto;
import com.terridb.libris.dtos.list.ListUpdateDto;
import com.terridb.libris.services.ListService;
import com.terridb.libris.utils.UrlUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lists")
public class ListController {

    private final ListService listService;

    public ListController(ListService listService) {
        this.listService = listService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListResponseDto> getListById(@PathVariable Long id) {
        return ResponseEntity.ok(listService.getListById(id));
    }

    @PostMapping
    public ResponseEntity<ListResponseDto> postList(@Valid @RequestBody ListRequestDto input) {
        ListResponseDto dto = listService.postList(input);
        return ResponseEntity.created(UrlUtil.generateUri("lists", dto.getId())).body(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ListResponseDto> updateList(@Valid @RequestBody ListUpdateDto input, @PathVariable Long id) {
        return ResponseEntity.ok(listService.updateList(id, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable Long id) {
        listService.deleteList(id);
        return ResponseEntity.noContent().build();
    }
}
