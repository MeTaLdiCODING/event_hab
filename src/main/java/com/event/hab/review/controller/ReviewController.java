package com.event.hab.review.controller;

import com.event.hab.review.DTO.ReviewRequest;
import com.event.hab.review.DTO.ReviewResponse;
import com.event.hab.review.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {
final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/events/{eventId}/reviews")
    public ReviewResponse createReview(@PathVariable Long eventId, @RequestBody ReviewRequest request){
       return reviewService.createReview(eventId, request);
    }

    @GetMapping("/events/{eventId}/reviews")
    public List<ReviewResponse> getReviewsOnEvent(@PathVariable Long eventId){
       return reviewService.getReviewsForEvent(eventId);
    }

    @GetMapping("/users/{userId}/reviews")
    public List<ReviewResponse> getReviewsOnOrganizer(@PathVariable Long userId){
        return reviewService.getReviewsForOrganizer(userId);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId){
        reviewService.deleteReview(reviewId);
    }


}
