package com.terridb.pagefolk.controllers;

import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.dtos.rating.RatingFilterDto;
import com.terridb.pagefolk.dtos.rating.RatingResponseDto;
import com.terridb.pagefolk.services.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @GetMapping
    public ResponseEntity<PaginatedResponse<RatingResponseDto>> getAllRatings(RatingFilterDto filter) {
        return ResponseEntity.ok(ratingService.getAllRatings(filter));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PaginatedResponse<RatingResponseDto>> getRatingsByUserId(
            @PathVariable Long userId,
            RatingFilterDto filter) {
        return ResponseEntity.ok(ratingService.getRatingsByUserId(userId, filter));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<PaginatedResponse<RatingResponseDto>> getRatingsByBookId(
            @PathVariable Long bookId,
            RatingFilterDto filter) {
        return ResponseEntity.ok(ratingService.getRatingsByBookId(bookId, filter));
    }
}
