export type NotificationType =
    | "NEW_BOOKING"
    | "BOOKING_ACCEPTED"
    | "BOOKING_REJECTED"
    | "RIDE_STARTED"
    | "RIDE_COMPLETED"
    | "RIDE_CANCELLED"
    | "PASSENGER_CANCELLED"
    | "NEW_REVIEW";

export interface Notification {
    id: string;
    type: NotificationType;
    title: string;
    message: string;
    link: string | null;
    isRead: boolean;
    createdAt: string;
}

export interface NotificationContextType {
    notifications: Notification[];
    unreadCount: number;
    isConnected: boolean;
    markAsRead: (id: string) => Promise<void>;
    markAllAsRead: () => Promise<void>;
    fetchNotifications: () => Promise<void>;
}
