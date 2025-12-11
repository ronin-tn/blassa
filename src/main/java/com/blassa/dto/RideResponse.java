package com.blassa.dto;

import com.blassa.model.enums.RideGenderPreference;
import com.blassa.model.enums.RideStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RideResponse(
        UUID id,
        String driverName, // mithel "Amine B."
        String driverEmail,
        Double driverRating,
        String driverFacebookUrl,
        String driverInstagramUrl,
        String driverPhoneNumber,

        String originName,
        Double originLat,
        Double originLon,

        String destinationName,
        Double destinationLat,
        Double destinationLon,

        OffsetDateTime departureTime,
        Integer totalSeats,
        Integer availableSeats,
        BigDecimal pricePerSeat,

        Boolean allowsSmoking,
        RideGenderPreference genderPreference,
        RideStatus status) {
}
