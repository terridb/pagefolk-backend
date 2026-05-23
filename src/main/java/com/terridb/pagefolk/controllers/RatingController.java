package com.terridb.pagefolk.controllers;

import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.dtos.rating.RatingFilterDto;
import com.terridb.pagefolk.dtos.rating.RatingRequestDto;
import com.terridb.pagefolk.dtos.rating.RatingResponseDto;
import com.terridb.pagefolk.dtos.rating.RatingUpdateRequestDto;
import com.terridb.pagefolk.services.RatingService;
import com.terridb.pagefolk.utils.UrlUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<PaginatedResponse<RatingResponseDto>> getAllRatings(RatingFilterDto filter) {
        return ResponseEntity.ok(ratingService.getAllRatings(filter));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<RatingResponseDto> getRatingById(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.getRatingById(id));
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

    @PostMapping
    public ResponseEntity<RatingResponseDto> createRating(@Valid @RequestBody RatingRequestDto input) {
        RatingResponseDto dto = ratingService.postRating(input);

        return ResponseEntity
                .created(UrlUtil.generateUri("ratings", dto.getId()))
                .body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingResponseDto> updateRating(
            @PathVariable Long id,
            @Valid @RequestBody RatingUpdateRequestDto input) {
        return ResponseEntity.ok(ratingService.updateRating(id, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}
