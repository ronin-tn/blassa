package com.blassa.repository;

import com.blassa.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    // Check if reviewer already reviewed this booking
    boolean existsByBookingIdAndReviewerId(UUID bookingId, UUID reviewerId);

    // Get reviews where user is the reviewee (received reviews)
    @EntityGraph(attributePaths = { "reviewer", "booking" })
    Page<Review> findByRevieweeId(UUID revieweeId, Pageable pageable);

    // Calculate average rating for a user (as reviewee)
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :userId")
    Optional<Double> calculateAverageRatingForUser(@Param("userId") UUID userId);

    // Count total reviews for a user
    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewee.id = :userId")
    Long countReviewsForUser(@Param("userId") UUID userId);

    // Get reviews where user is the reviewer (sent reviews)
    @EntityGraph(attributePaths = { "reviewee", "booking" })
    Page<Review> findByReviewerId(UUID reviewerId, Pageable pageable);
}
