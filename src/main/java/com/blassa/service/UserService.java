package com.blassa.service;

import com.blassa.dto.ChangePasswordRequest;
import com.blassa.dto.Profile;
import com.blassa.dto.ProfileUpdateRequest;
import com.blassa.dto.PublicProfileResponse;
import com.blassa.model.entity.User;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final com.blassa.repository.RideRepository rideRepository;
    private final com.blassa.repository.ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    public Profile getProfile() {
        User user = getCurrentUser();
        return mapToProfile(user);
    }

    @Transactional
    public Profile updateProfile(ProfileUpdateRequest request) {
        User user = getCurrentUser();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBio(request.getBio());
        user.setFacebookUrl(request.getFacebookUrl());
        user.setInstagramUrl(request.getInstagramUrl());

        // Update dob and gender if provided (hedhy lel gmail users completing profile)
        if (request.getDob() != null) {
            user.setDateOfBirth(request.getDob());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }

        User savedUser = userRepository.save(user);
        return mapToProfile(savedUser);
    }

    //Upload w updati PDP
    @Transactional
    public Profile updateProfilePicture(MultipartFile file) throws IOException {
        User user = getCurrentUser();
        // Upload to Cloudinary
        String imageUrl = cloudinaryService.uploadProfilePicture(file, user.getId());
        // Update PDP t3 User
        user.setProfilePictureUrl(imageUrl);
        User savedUser = userRepository.save(user);
        return mapToProfile(savedUser);
    }

    //Supprimi PDP
    @Transactional
    public Profile removeProfilePicture() throws IOException {
        User user = getCurrentUser();

        // Delete from Cloudinary if exists
        if (user.getProfilePictureUrl() != null) {
            cloudinaryService.deleteProfilePicture(user.getId());
        }

        //Clear el URL
        user.setProfilePictureUrl(null);
        User savedUser = userRepository.save(user);

        return mapToProfile(savedUser);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();

        // Validate current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Le mot de passe actuel est incorrect");
        }

        // Validate new password matches confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
//DELETE User mn platform
    @Transactional
    public void deleteAccount() {
        User user = getCurrentUser();
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
                user.getInstagramUrl());
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
