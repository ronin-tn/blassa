package com.blassa.service;

import com.blassa.dto.NotificationResponse;
import com.blassa.model.entity.User;
import com.blassa.model.enums.NotificationType;
import com.blassa.notification.Notification;
import com.blassa.repository.NotificationRepository;
import com.blassa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void sendNotification(
            UUID recipientId,
            NotificationType type,
            String title,
            String message,
            String link) {
        User recipient = userRepository.findById(recipientId).orElseThrow();
        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .title(title)
                .message(message)
                .link(link)
                .isRead(false)
                .createdAt(OffsetDateTime.now())
                .build();
        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = mapToResponse(saved);

        // Send to user's email - matches the WebSocket principal name
        simpMessagingTemplate
                .convertAndSendToUser(recipient.getEmail(),
                        "/queue/notification",
                        response);
    }

    public NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getLink(),
                notification.isRead(),
                notification.getCreatedAt());
    }

    public List<NotificationResponse> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<NotificationResponse> getAllNotifications(UUID userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(UUID userId) {
        List<Notification> notifications = notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

}
