package com.blassa.controller;

import com.blassa.dto.ChangePasswordRequest;
import com.blassa.dto.Profile;
import com.blassa.dto.ProfileUpdateRequest;
import com.blassa.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
