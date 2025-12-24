package com.blassa.controller;

import com.blassa.dto.ChangePasswordRequest;
import com.blassa.dto.Profile;
import com.blassa.dto.ProfileUpdateRequest;
import com.blassa.dto.PublicProfileResponse;
import com.blassa.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<Profile> getMyProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<Profile> updateMyProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    @PostMapping(value = "/me/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Profile> uploadProfilePicture(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(userService.updateProfilePicture(file));
    }

    @DeleteMapping("/me/picture")
    public ResponseEntity<Profile> deleteProfilePicture() throws IOException {
        return ResponseEntity.ok(userService.removeProfilePicture());
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Mot de passe modifié avec succès");
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteAccount() {
        userService.deleteAccount();
        return ResponseEntity.ok("Compte supprimé avec succès");
    }

    @GetMapping("/{userId}/public")
    public ResponseEntity<PublicProfileResponse> getPublicProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getPublicProfile(userId));
    }
}
