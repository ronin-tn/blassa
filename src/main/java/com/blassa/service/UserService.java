package com.blassa.service;

import com.blassa.dto.Profile;
import com.blassa.dto.ProfileUpdateRequest;
import com.blassa.model.entity.User;
import com.blassa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

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

        User savedUser = userRepository.save(user);
        return mapToProfile(savedUser);
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

    private User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
