package com.blassa.dto;


import com.blassa.model.enums.BookingStatus;

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
        OffsetDateTime createdAt) {
}
