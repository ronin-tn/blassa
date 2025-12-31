package com.blassa.dto;

import com.blassa.model.enums.RideGenderPreference;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RideRequest(
        @NotNull String originName,
        @NotNull Double originLat,
        @NotNull Double originLon,

        @NotNull String destinationName,
        @NotNull Double destinationLat,
        @NotNull Double destinationLon,

        @NotNull @Future OffsetDateTime departureTime,
        @NotNull Integer totalSeats,
        @NotNull BigDecimal pricePerSeat,

        Boolean allowsSmoking,
        Boolean allowsMusic,
        Boolean allowsPets,
        String luggageSize,
        RideGenderPreference genderPreference,
        java.util.UUID vehicleId) {
}
