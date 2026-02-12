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

        // Jib booking
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new IllegalArgumentException("BOOKING_NOT_FOUND"));

        // Verifi règles mta3 review
        validateReviewRules(booking, reviewer);

        // Shkoun reviewee (l partie lokhra)
        User reviewee = determineReviewee(booking, reviewer);

        // Checki kene 3mal review deja
        if (reviewRepository.existsByBookingIdAndReviewerId(booking.getId(), reviewer.getId())) {
            throw new IllegalArgumentException("ALREADY_REVIEWED");
        }

        // Asna3 review
        Review review = new Review();
        review.setBooking(booking);
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setRating(request.rating());
        review.setComment(request.comment());

        Review saved = reviewRepository.save(review);

        // Ab3ath notification lel reviewee
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
     * /**
     * Jib reviews li t'harhom user l 7ali.
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyReceivedReviews(int page, int size) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByRevieweeId(currentUser.getId(), pageable)
                .map(this::mapToResponse);
    }

    /**
     * Jib reviews li b3athom user l 7ali.
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
        // Booking lezm ykoun CONFIRMED
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("BOOKING_NOT_CONFIRMED");
        }
        //for tests:
    //Booking status(CONFIRMED) ==>Booking id,booking ride, booking status,
        // /Ride Status(COMPLETED)==>ride id, ride status,driver
        //Driver==> driver id
        //Passenger==>passenger id,
        // Ride lezm tkoun COMPLETED
        //user reviewer==>reviewer ID
        if (booking.getRide().getStatus() != RideStatus.COMPLETED) {
            throw new IllegalArgumentException("RIDE_NOT_COMPLETED");
        }

        // Reviewer lezm ykoun partie mel booking (driver walla passenger)
        UUID passengerId = booking.getPassenger().getId();
        UUID driverId = booking.getRide().getDriver().getId();
        UUID reviewerId = reviewer.getId();

        if (!reviewerId.equals(passengerId) && !reviewerId.equals(driverId)) {
            throw new IllegalArgumentException("NOT_PART_OF_BOOKING");
        }
    }

    private User determineReviewee(Booking booking, User reviewer) {
        // Kene reviewer huwwa passenger -> reviewee huwwa driver
        // Kene reviewer huwwa driver -> reviewee huwwa passenger
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
