"use client";

import { useState, useRef, useEffect } from "react";
import Link from "next/link";
import { Bell, Check, CheckCheck, Car, Ticket, X, Loader2, Star } from "lucide-react";
import { useNotifications } from "@/contexts/NotificationContext";
import { Notification, NotificationType } from "@/types/notification";
import { cn } from "@/lib/utils";


function getNotificationIcon(type: NotificationType) {
    switch (type) {
        case "NEW_BOOKING":
        case "BOOKING_ACCEPTED":
        case "BOOKING_REJECTED":
        case "PASSENGER_CANCELLED":
            return <Ticket className="w-4 h-4" />;
        case "RIDE_STARTED":
        case "RIDE_COMPLETED":
        case "RIDE_CANCELLED":
            return <Car className="w-4 h-4" />;
        case "NEW_REVIEW":
            return <Star className="w-4 h-4" />;
        default:
            return <Bell className="w-4 h-4" />;
    }
}

function getIconBgColor(type: NotificationType) {
    switch (type) {
        case "NEW_BOOKING":
            return "bg-blue-100 text-blue-600";
        case "BOOKING_ACCEPTED":
        case "RIDE_COMPLETED":
            return "bg-green-100 text-green-600";
        case "BOOKING_REJECTED":
        case "RIDE_CANCELLED":
        case "PASSENGER_CANCELLED":
            return "bg-red-100 text-red-600";
        case "RIDE_STARTED":
            return "bg-amber-100 text-amber-600";
        case "NEW_REVIEW":
            return "bg-yellow-100 text-yellow-600";
        default:
            return "bg-slate-100 text-slate-600";
    }
}

function formatRelativeTime(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffMins < 1) return "À l'instant";
    if (diffMins < 60) return `Il y a ${diffMins} min`;
    if (diffHours < 24) return `Il y a ${diffHours}h`;
    if (diffDays < 7) return `Il y a ${diffDays}j`;
    return date.toLocaleDateString("fr-FR", { day: "numeric", month: "short" });
}

function NotificationItem({
    notification,
    onMarkAsRead,
    onClose,
}: {
    notification: Notification;
    onMarkAsRead: (id: string) => void;
    onClose: () => void;
}) {
    const handleClick = () => {
        if (!notification.isRead) {
            onMarkAsRead(notification.id);
        }
        if (notification.link) {
            onClose();
        }
    };

    const content = (
        <div
            className={cn(
                "flex items-start gap-3 p-3 transition-colors cursor-pointer relative",
                !notification.isRead
                    ? "bg-[#006B8F]/5 hover:bg-[#006B8F]/10 border-l-3 border-l-[#006B8F]"
                    : "bg-white hover:bg-slate-50 opacity-75"
            )}
            onClick={handleClick}
        >
            <div
                className={cn(
                    "shrink-0 w-8 h-8 rounded-full flex items-center justify-center",
                    !notification.isRead
                        ? getIconBgColor(notification.type)
                        : "bg-slate-100 text-slate-400"
                )}
            >
                {notification.isRead ? (
                    <Check className="w-4 h-4" />
                ) : (
                    getNotificationIcon(notification.type)
                )}
            </div>

            <div className="flex-1 min-w-0">
                <p
                    className={cn(
                        "text-sm",
                        !notification.isRead
                            ? "font-semibold text-slate-900"
                            : "font-normal text-slate-500"
                    )}
                >
                    {notification.title}
                </p>
                <p className={cn(
                    "text-xs mt-0.5 line-clamp-2",
                    !notification.isRead ? "text-slate-600" : "text-slate-400"
                )}>
                    {notification.message}
                </p>
                <p className="text-xs text-slate-400 mt-1">
                    {formatRelativeTime(notification.createdAt)}
                    {notification.isRead && (
                        <span className="ml-2 text-green-500">• Lu</span>
                    )}
                </p>
            </div>

            {!notification.isRead && (
                <div className="shrink-0 w-2.5 h-2.5 bg-[#006B8F] rounded-full mt-2 animate-pulse" />
            )}
        </div>
    );

    if (notification.link) {
        return <Link href={notification.link}>{content}</Link>;
    }

    return content;
}

export default function NotificationDropdown() {
    const { notifications, unreadCount, markAsRead, markAllAsRead, isConnected } =
        useNotifications();
    const [isOpen, setIsOpen] = useState(false);
    const [isMarkingAll, setIsMarkingAll] = useState(false);
    const dropdownRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (
                dropdownRef.current &&
                !dropdownRef.current.contains(event.target as Node)
            ) {
                setIsOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const handleMarkAllAsRead = async () => {
        setIsMarkingAll(true);
        await markAllAsRead();
        setIsMarkingAll(false);
    };

    return (
        <div className="relative" ref={dropdownRef}>
            <button
                onClick={() => setIsOpen(!isOpen)}
                className={cn(
                    "relative p-2 rounded-full transition-colors",
                    isOpen
                        ? "text-[#006B8F] bg-[#006B8F]/10"
                        : "text-slate-500 hover:text-[#006B8F] hover:bg-slate-50"
                )}
                aria-label="Notifications"
            >
                <Bell className="w-5 h-5" />

                {unreadCount > 0 && (
                    <span className="absolute -top-0.5 -right-0.5 min-w-[18px] h-[18px] px-1 bg-[#FF9A3E] text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                        {unreadCount > 99 ? "99+" : unreadCount}
                    </span>
                )}

                {isConnected && (
                    <span className="absolute bottom-0.5 right-0.5 w-2 h-2 bg-green-500 rounded-full border border-white" />
                )}
            </button>

            {isOpen && (
                <div className="absolute right-0 mt-2 w-80 sm:w-96 bg-white rounded-xl shadow-lg border border-slate-100 overflow-hidden z-50 animate-in fade-in slide-in-from-top-2 duration-200">
                    <div className="flex items-center justify-between px-4 py-3 border-b border-slate-100">
                        <h3 className="font-semibold text-slate-900">Notifications</h3>
                        <div className="flex items-center gap-2">
                            {unreadCount > 0 && (
                                <button
                                    onClick={handleMarkAllAsRead}
                                    disabled={isMarkingAll}
                                    className="text-xs text-[#006B8F] hover:underline flex items-center gap-1 disabled:opacity-50"
                                >
                                    {isMarkingAll ? (
                                        <Loader2 className="w-3 h-3 animate-spin" />
                                    ) : (
                                        <CheckCheck className="w-3 h-3" />
                                    )}
                                    Tout marquer comme lu
                                </button>
                            )}
                            <button
                                onClick={() => setIsOpen(false)}
                                className="p-1 text-slate-400 hover:text-slate-600 rounded"
                            >
                                <X className="w-4 h-4" />
                            </button>
                        </div>
                    </div>

                    <div className="max-h-[400px] overflow-y-auto">
                        {notifications.length === 0 ? (
                            <div className="py-12 text-center">
                                <Bell className="w-10 h-10 text-slate-300 mx-auto mb-3" />
                                <p className="text-sm text-slate-500">
                                    Aucune notification
                                </p>
                            </div>
                        ) : (
                            <div className="divide-y divide-slate-100">
                                {notifications.map((notification) => (
                                    <NotificationItem
                                        key={notification.id}
                                        notification={notification}
                                        onMarkAsRead={markAsRead}
                                        onClose={() => setIsOpen(false)}
                                    />
                                ))}
                            </div>
                        )}
                    </div>

                    {notifications.length > 0 && (
                        <div className="border-t border-slate-100 p-2">
                            <Link
                                href="/dashboard/notifications"
                                onClick={() => setIsOpen(false)}
                                className="block text-center text-sm text-[#006B8F] hover:bg-[#006B8F]/5 py-2 rounded-lg transition-colors"
                            >
                                Voir toutes les notifications
                            </Link>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}
