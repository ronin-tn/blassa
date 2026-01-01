package com.blassa.repository;

import com.blassa.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    boolean existsByBookingIdAndReviewerId(UUID bookingId, UUID reviewerId);

    @EntityGraph(attributePaths = { "reviewer", "booking" })
    Page<Review> findByRevieweeId(UUID revieweeId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :userId")
    Optional<Double> calculateAverageRatingForUser(@Param("userId") UUID userId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewee.id = :userId")
    Long countReviewsForUser(@Param("userId") UUID userId);

    @EntityGraph(attributePaths = { "reviewee", "booking" })
    Page<Review> findByReviewerId(UUID reviewerId, Pageable pageable);

    List<Review> findByReviewerId(UUID reviewerId);

    List<Review> findByRevieweeId(UUID revieweeId);
}
