package com.blassa.controller;

import com.blassa.dto.ReviewRequest;
import com.blassa.dto.ReviewResponse;
import com.blassa.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody @Valid ReviewRequest request) {
        return ResponseEntity.ok(reviewService.createReview(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsForUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getReviewsForUser(userId, page, size));
    }

    @GetMapping("/user/{userId}/rating")
    public ResponseEntity<Map<String, Object>> getUserRating(@PathVariable UUID userId) {
        Double avgRating = reviewService.getAverageRatingForUser(userId);
        Long count = reviewService.getReviewCountForUser(userId);
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "averageRating", Math.round(avgRating * 10.0) / 10.0, // Round to 1 decimal
                "totalReviews", count));
    }

    /**
     * Get reviews received by the current user.
     */
    @GetMapping("/mine/received")
    public ResponseEntity<Page<ReviewResponse>> getMyReceivedReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getMyReceivedReviews(page, size));
    }

    /**
     * Get reviews sent by the current user.
     */
    @GetMapping("/mine/sent")
    public ResponseEntity<Page<ReviewResponse>> getMySentReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getMySentReviews(page, size));
    }
}
