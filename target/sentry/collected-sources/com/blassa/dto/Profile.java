package com.blassa.dto;

import com.blassa.model.enums.Gender;
import com.blassa.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Profile {
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dob;
    private Gender gender;
    private String phoneNumber;
    private String bio;
    private String profilePictureUrl;
    private String facebookUrl;
    private String instagramUrl;
    private UserRole role;
}
