package com.blassa.dto;

import com.blassa.model.enums.NotificationType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationResponse(
                UUID id,
                NotificationType type,
                String title,
                String message,
                String link,
                boolean isRead,
                OffsetDateTime createdAt) {
}
