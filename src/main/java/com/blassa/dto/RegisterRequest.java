package com.blassa.dto;

import com.blassa.model.enums.Gender;
import com.blassa.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email is required") @Email
    private String email;
    @NotBlank(message="Password is required")
    private String password;
    @NotBlank(message="first name is required")
    private String firstName;
    @NotBlank(message="Last Name is required")
    private String lastName;
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+[1-9][0-9]{7,14}$", message = "Phone must be E.164 format (e.g., +21612345678)")
    private String phoneNumber;
    @NotNull(message = "Gender mustn't be null")
    private Gender gender;
    @NotNull(message = "Date of Birth must not be null")
    private LocalDate birthDate;
    private UserRole role=UserRole.USER;


}
