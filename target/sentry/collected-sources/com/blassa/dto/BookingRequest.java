package com.blassa.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookingRequest(
        @NotNull UUID rideId,
        @Min(1) @Max(4) int seatsRequested
        ) {
}
