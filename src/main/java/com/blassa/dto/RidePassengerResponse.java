package com.blassa.dto;

import com.blassa.model.enums.BookingStatus;
import java.util.UUID;

public record RidePassengerResponse(
                UUID bookingId,
                UUID passengerId,
                String passengerName,
                String passengerEmail,
                String passengerPhone,
                String passengerProfilePictureUrl,
                String facebookUrl,
                String instagramUrl,
                Integer seatsBooked,
                BookingStatus status) {
}
