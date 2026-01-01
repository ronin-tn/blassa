package com.blassa.service;

import com.blassa.dto.ChangeEmailRequest;
import com.blassa.dto.ChangePasswordRequest;
import com.blassa.dto.Profile;
import com.blassa.dto.ProfileUpdateRequest;
import com.blassa.dto.PublicProfileResponse;
import com.blassa.model.entity.User;
import com.blassa.model.entity.Ride;
import com.blassa.model.entity.Booking;
import com.blassa.model.entity.Review;
import com.blassa.model.enums.RideStatus;
import com.blassa.model.enums.BookingStatus;
import com.blassa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final com.blassa.repository.RideRepository rideRepository;
    private final com.blassa.repository.ReviewRepository reviewRepository;
    private final com.blassa.repository.BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;
    private final com.blassa.security.JwtUtils jwtUtils;

    public Profile getProfile() {
        User user = getCurrentUser();
        return mapToProfile(user);
    }

    @Transactional
    public Profile updateProfile(ProfileUpdateRequest request) {
        User user = getCurrentUser();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new RuntimeException("Ce numéro de téléphone existe déjà");
            }
        }
        user.setPhoneNumber(request.getPhoneNumber());

        user.setBio(request.getBio());
        user.setFacebookUrl(request.getFacebookUrl());
        user.setInstagramUrl(request.getInstagramUrl());

        if (request.getDob() != null) {
            if (java.time.Period.between(request.getDob(), java.time.LocalDate.now()).getYears() < 18) {
                throw new RuntimeException("Vous devez avoir au moins 18 ans pour utiliser Blassa");
            }
            user.setDateOfBirth(request.getDob());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }

        User savedUser = userRepository.save(user);
        return mapToProfile(savedUser);
    }

    public Profile updateProfilePicture(MultipartFile file) throws IOException {
        User user = getCurrentUser();
        String imageUrl = cloudinaryService.uploadProfilePicture(file, user.getId());
        user.setProfilePictureUrl(imageUrl);
        User savedUser = userRepository.save(user);
        return mapToProfile(savedUser);
    }

    @Transactional
    public Profile removeProfilePicture() throws IOException {
        User user = getCurrentUser();

        if (user.getProfilePictureUrl() != null) {
            cloudinaryService.deleteProfilePicture(user.getId());
        }

        user.setProfilePictureUrl(null);
        User savedUser = userRepository.save(user);

        return mapToProfile(savedUser);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Le mot de passe actuel est incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public Profile changeEmail(ChangeEmailRequest request) {
        User user = getCurrentUser();

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Le mot de passe est incorrect");
        }

        // Check if new email is same as current
        if (user.getEmail().equalsIgnoreCase(request.getNewEmail())) {
            throw new IllegalArgumentException("Le nouvel email doit être différent de l'email actuel");
        }

        // Check if new email is already in use
        if (userRepository.existsByEmail(request.getNewEmail())) {
            throw new IllegalArgumentException("Cette adresse email est déjà utilisée");
        }

        // Update email and mark as unverified
        user.setEmail(request.getNewEmail());
        user.setVerified(false);

        // Generate new verification token and send email
        var newToken = jwtUtils.generateToken(
                org.springframework.security.core.userdetails.User.builder()
                        .username(request.getNewEmail())
                        .password(user.getPasswordHash())
                        .authorities("USER")
                        .build());
        user.setVerificationToken(newToken);
        user.setVerificationSentAt(java.time.LocalDateTime.now());

        User savedUser = userRepository.save(user);
        emailService.sendVerificationEmail(request.getNewEmail(), newToken);

        return mapToProfile(savedUser);
    }

    @Transactional
    public void deleteAccount() {
        User user = getCurrentUser();
        long activeRides = rideRepository.countByDriverIdAndStatusIn(user.getId(),
                List.of(RideStatus.SCHEDULED, RideStatus.IN_PROGRESS));

        if (activeRides > 0) {
            throw new IllegalArgumentException(
                    "Vous ne pouvez pas supprimer votre compte tant que vous avez des trajets en cours ou planifiés. Veuillez les annuler ou les terminer.");
        }

        long activeBookings = bookingRepository.countByPassengerIdAndStatusIn(user.getId(),
                List.of(BookingStatus.CONFIRMED));

        if (activeBookings > 0) {
            throw new IllegalArgumentException(
                    "Vous ne pouvez pas supprimer votre compte tant que vous avez des réservations actives. Veuillez les annuler.");
        }

        List<Review> userReviews = reviewRepository.findByReviewerId(user.getId());
        reviewRepository.deleteAll(userReviews);

        List<Review> reviewsAboutUser = reviewRepository.findByRevieweeId(user.getId());
        reviewRepository.deleteAll(reviewsAboutUser);

        List<Booking> userBookings = bookingRepository.findByPassengerId(user.getId());
        bookingRepository.deleteAll(userBookings);

        List<Ride> userRides = rideRepository.findByDriverId(user.getId());
        for (Ride ride : userRides) {
            List<Booking> rideBookings = bookingRepository.findByRideId(ride.getId());
            bookingRepository.deleteAll(rideBookings);
            rideRepository.delete(ride);
        }

        try {
            if (user.getProfilePictureUrl() != null) {
                cloudinaryService.deleteProfilePicture(user.getId());
            }
        } catch (Exception e) {
            log.error("Failed to delete profile picture for user {}", user.getId(), e);
        }

        userRepository.delete(user);
    }

    private Profile mapToProfile(User user) {
        return new Profile(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getGender(),
                user.getPhoneNumber(),
                user.getBio(),
                user.getProfilePictureUrl(),
                user.getFacebookUrl(),
                user.getInstagramUrl(),
                user.getRole());
    }

    public PublicProfileResponse getPublicProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int completedRides = rideRepository.countByDriverIdAndStatus(userId,
                com.blassa.model.enums.RideStatus.COMPLETED);
        Double averageRating = reviewRepository.calculateAverageRatingForUser(userId).orElse(null);

        return new PublicProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBio(),
                user.getProfilePictureUrl(),
                user.getGender(),
                user.getCreatedAt() != null ? user.getCreatedAt().toLocalDate() : java.time.LocalDate.now(),
                completedRides,
                averageRating);
    }

    private User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
