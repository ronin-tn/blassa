package com.blassa.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID bookingId,
        String reviewerName,
        String revieweeName,
        Integer rating,
        String comment,
        OffsetDateTime createdAt) {
}
