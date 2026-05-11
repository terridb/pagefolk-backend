package com.terridb.pagefolk.controllers;

import com.terridb.pagefolk.dtos.bookseries.BookSeriesRequestDto;
import com.terridb.pagefolk.dtos.bookseries.BookSeriesResponseDto;
import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.services.BookSeriesService;
import com.terridb.pagefolk.utils.UrlUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/series")
public class BookSeriesController {

    private final BookSeriesService bookSeriesService;

    public BookSeriesController(BookSeriesService bookSeriesService) {
        this.bookSeriesService = bookSeriesService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<BookSeriesResponseDto>> getAllBookSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(bookSeriesService.getAllBookSeries(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookSeriesResponseDto> getBookSeriesById(@PathVariable Long id) {
        return ResponseEntity.ok(bookSeriesService.getBookSeriesById(id));
    }

    @PostMapping
    public ResponseEntity<BookSeriesResponseDto> postBookSeries(@Valid @RequestBody BookSeriesRequestDto input) {
        BookSeriesResponseDto dto = bookSeriesService.postBookSeries(input);
        return ResponseEntity.created(UrlUtil.generateUri("series", dto.getId())).body(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookSeriesResponseDto> updateBookSeries(@Valid @RequestBody BookSeriesRequestDto input, @PathVariable Long id) {
        return ResponseEntity.ok(bookSeriesService.updateBookSeries(id, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookSeries(@PathVariable Long id) {
        bookSeriesService.deleteBookSeries(id);
        return ResponseEntity.noContent().build();
    }
}
