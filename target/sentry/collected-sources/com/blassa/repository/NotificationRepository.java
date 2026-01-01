package com.blassa.repository;

import com.blassa.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(UUID recipientId);

    long countByRecipientIdAndIsReadFalse(UUID recipientId);

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);
}
