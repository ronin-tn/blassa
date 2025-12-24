package com.blassa.dto;

import com.blassa.model.enums.Gender;
import java.time.LocalDate;
import java.util.UUID;

public record PublicProfileResponse(
                UUID id,
                String firstName,
                String lastName,
                String bio,
                String profilePictureUrl,
                Gender gender,
                LocalDate memberSince,
                Integer completedRidesCount,
                Double averageRating) {
}
