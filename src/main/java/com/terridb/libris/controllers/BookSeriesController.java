package com.terridb.libris.controllers;

import com.terridb.libris.dtos.bookseries.BookSeriesResponseDto;
import com.terridb.libris.dtos.common.PaginatedResponse;
import com.terridb.libris.services.BookSeriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
