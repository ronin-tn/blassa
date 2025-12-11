package com.blassa.dto;

import com.blassa.model.enums.BookingStatus;
import java.util.UUID;

/**
 * DTO for ride passengers, used by drivers to see who booked their ride.
 * Includes contact and social media info, and booking status.
 */
public record RidePassengerResponse(
                UUID bookingId,
                String passengerName,
                String passengerEmail,
                String passengerPhone,
                String facebookUrl,
                String instagramUrl,
                Integer seatsBooked,
                BookingStatus status) {
}
