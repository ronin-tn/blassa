package com.blassa.repository;

import com.blassa.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    // Find unread notifications for a specific user
    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(UUID recipientId);

    // Count unread notifications for a specific user
    long countByRecipientIdAndIsReadFalse(UUID recipientId);

    // Find all notifications for a user (by recipient ID)
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);
}
