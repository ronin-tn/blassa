"use client";

import {
    createContext,
    useContext,
    useState,
    useEffect,
    useCallback,
    useRef,
    ReactNode,
} from "react";
import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { useAuth } from "@/contexts/AuthContext";
import { Notification, NotificationContextType } from "@/types/notification";
import { API_URL, WS_URL } from "@/lib/config";

const NotificationContext = createContext<NotificationContextType | undefined>(
    undefined
);

export function NotificationProvider({ children }: { children: ReactNode }) {
    const { isAuthenticated, user } = useAuth();
    const [notifications, setNotifications] = useState<Notification[]>([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [isConnected, setIsConnected] = useState(false);
    const stompClientRef = useRef<Client | null>(null);

    // Fetch all notifications from API (both read and unread)
    const fetchNotifications = useCallback(async () => {
        if (!isAuthenticated) return;

        try {
            const response = await fetch(`${API_URL}/notifications`, {
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
                const data: Notification[] = await response.json();
                setNotifications(data);
                // Calculate unread count from fetched data
                setUnreadCount(data.filter(n => !n.isRead).length);
            }
        } catch (error) {
            console.error("Failed to fetch notifications:", error);
        }
    }, [isAuthenticated]);



    // Mark single notification as read
    const markAsRead = useCallback(
        async (id: string) => {
            if (!isAuthenticated) return;

            try {
                await fetch(`${API_URL}/notifications/${id}/read`, {
                    method: "POST",
                    credentials: "include",
                    headers: {
                        "Content-Type": "application/json",
                    },
                });

                setNotifications((prev) =>
                    prev.map((n) => (n.id === id ? { ...n, isRead: true } : n))
                );
                setUnreadCount((prev) => Math.max(0, prev - 1));
            } catch (error) {
                console.error("Failed to mark notification as read:", error);
            }
        },
        [isAuthenticated]
    );

    // Mark all notifications as read
    const markAllAsRead = useCallback(async () => {
        if (!isAuthenticated) return;

        try {
            await fetch(`${API_URL}/notifications/read-all`, {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
            setUnreadCount(0);
        } catch (error) {
            console.error("Failed to mark all notifications as read:", error);
        }
    }, [isAuthenticated]);

    // Handle incoming WebSocket notification
    const handleNotification = useCallback((message: IMessage) => {
        try {
            const notification: Notification = JSON.parse(message.body);
            setNotifications((prev) => [notification, ...prev]);
            setUnreadCount((prev) => prev + 1);
        } catch (error) {
            console.error("Failed to parse notification:", error);
        }
    }, []);

    // Fetch initial notifications when authenticated
    useEffect(() => {
        if (isAuthenticated) {
            // eslint-disable-next-line react-hooks/set-state-in-effect
            fetchNotifications();
        }
    }, [isAuthenticated, fetchNotifications]);

    // Setup WebSocket connection
    useEffect(() => {
        if (!isAuthenticated || !user) {
            return;
        }

        // Create STOMP client
        const client = new Client({
            webSocketFactory: () => new SockJS(WS_URL),
            debug: (str) => {
                if (process.env.NODE_ENV === "development") {
                    console.log("[STOMP]", str);
                }
            },
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        client.onConnect = () => {
            setIsConnected(true);

            // Subscribe to user-specific notification queue
            client.subscribe("/user/queue/notification", handleNotification);
        };

        client.onDisconnect = () => {
            setIsConnected(false);
        };

        client.onStompError = (frame) => {
            console.error("STOMP error:", frame.headers["message"]);
            setIsConnected(false);
        };

        client.activate();
        stompClientRef.current = client;

        return () => {
            if (stompClientRef.current) {
                stompClientRef.current.deactivate();
                stompClientRef.current = null;
                setIsConnected(false);
            }
        };
    }, [isAuthenticated, user, handleNotification]);

    const value: NotificationContextType = {
        notifications,
        unreadCount,
        isConnected,
        markAsRead,
        markAllAsRead,
        fetchNotifications,
    };

    return (
        <NotificationContext.Provider value={value}>
            {children}
        </NotificationContext.Provider>
    );
}

export function useNotifications() {
    const context = useContext(NotificationContext);
    if (context === undefined) {
        throw new Error(
            "useNotifications must be used within a NotificationProvider"
        );
    }
    return context;
}
