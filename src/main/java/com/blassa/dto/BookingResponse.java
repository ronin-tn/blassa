package com.blassa.dto;

import com.blassa.model.enums.BookingStatus;
import com.blassa.model.enums.RideStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID rideID,
        String rideSummary,
        String driverName,
        OffsetDateTime departureTime,
        Integer seatsBooked,
        BigDecimal priceTotal,
        BookingStatus status,
        RideStatus rideStatus,
        OffsetDateTime createdAt,
        String carLicensePlate,
        String carDescription) {
}
