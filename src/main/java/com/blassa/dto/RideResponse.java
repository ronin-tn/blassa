package com.blassa.dto;

import com.blassa.model.enums.RideGenderPreference;
import com.blassa.model.enums.RideStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RideResponse(
        UUID id,
        String driverName, // mithel "Amine B."
        Double driverRating,

        String originName,
        Double originLat,
        Double originLon,

        String destinationName,
        Double destinationLat,
        Double destinationLon,

        OffsetDateTime departureTime,
        Integer availableSeats,
        BigDecimal pricePerSeat,

        Boolean allowsSmoking,
        RideGenderPreference genderPreference,
        RideStatus status
) {
}
