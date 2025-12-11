package com.blassa.service;

import com.blassa.dto.ReviewRequest;
import com.blassa.dto.ReviewResponse;
import com.blassa.model.entity.Booking;
import com.blassa.model.entity.Review;
import com.blassa.model.entity.User;
import com.blassa.model.enums.BookingStatus;
import com.blassa.model.enums.RideStatus;
import com.blassa.repository.BookingRepository;
import com.blassa.repository.ReviewRepository;
import com.blassa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        User reviewer = getCurrentUser();

        // Fetch booking
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new IllegalArgumentException("BOOKING_NOT_FOUND"));

        // Validate review rules
        validateReviewRules(booking, reviewer);

        // Determine reviewee (the other party)
        User reviewee = determineReviewee(booking, reviewer);

        // Check for duplicate review
        if (reviewRepository.existsByBookingIdAndReviewerId(booking.getId(), reviewer.getId())) {
            throw new IllegalArgumentException("ALREADY_REVIEWED");
        }

        // Create review
        Review review = new Review();
        review.setBooking(booking);
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setRating(request.rating());
        review.setComment(request.comment());

        Review saved = reviewRepository.save(review);

        // Send notification to reviewee
        String reviewerName = reviewer.getFirstName() + " " + reviewer.getLastName();
        notificationService.sendNotification(
                reviewee.getId(),
                com.blassa.model.enums.NotificationType.NEW_REVIEW,
                "Nouvel avis reçu",
                reviewerName + " vous a donné " + request.rating() + " étoile" + (request.rating() > 1 ? "s" : ""),
                "/dashboard/reviews");

        return mapToResponse(saved);
    }

    @Transactional
    public Page<ReviewResponse> getReviewsForUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByRevieweeId(userId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get reviews received by the current user.
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyReceivedReviews(int page, int size) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByRevieweeId(currentUser.getId(), pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get reviews sent by the current user.
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMySentReviews(int page, int size) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByReviewerId(currentUser.getId(), pageable)
                .map(this::mapToResponse);
    }

    public Double getAverageRatingForUser(UUID userId) {
        return reviewRepository.calculateAverageRatingForUser(userId).orElse(0.0);
    }

    public Long getReviewCountForUser(UUID userId) {
        return reviewRepository.countReviewsForUser(userId);
    }

    private void validateReviewRules(Booking booking, User reviewer) {
        // Booking must be CONFIRMED
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("BOOKING_NOT_CONFIRMED");
        }

        // Ride must be COMPLETED
        if (booking.getRide().getStatus() != RideStatus.COMPLETED) {
            throw new IllegalArgumentException("RIDE_NOT_COMPLETED");
        }

        // Reviewer must be part of the booking (driver or passenger)
        UUID passengerId = booking.getPassenger().getId();
        UUID driverId = booking.getRide().getDriver().getId();
        UUID reviewerId = reviewer.getId();

        if (!reviewerId.equals(passengerId) && !reviewerId.equals(driverId)) {
            throw new IllegalArgumentException("NOT_PART_OF_BOOKING");
        }
    }

    private User determineReviewee(Booking booking, User reviewer) {
        // If reviewer is the passenger -> reviewee is the driver
        // If reviewer is the driver -> reviewee is the passenger
        if (reviewer.getId().equals(booking.getPassenger().getId())) {
            return booking.getRide().getDriver();
        } else {
            return booking.getPassenger();
        }
    }

    private ReviewResponse mapToResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getBooking().getId(),
                review.getReviewer().getFirstName() + " " + review.getReviewer().getLastName(),
                review.getReviewee().getFirstName() + " " + review.getReviewee().getLastName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt());
    }

    private User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
