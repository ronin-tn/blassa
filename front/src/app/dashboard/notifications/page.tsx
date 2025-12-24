"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
    Bell,
    Check,
    CheckCheck,
    Car,
    Ticket,
    ArrowLeft,
    Loader2,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import { useNotifications } from "@/contexts/NotificationContext";
import Navbar from "@/components/layout/Navbar";
import { Notification, NotificationType } from "@/types/notification";
import { cn } from "@/lib/utils";

// Get icon based on notification type
function getNotificationIcon(type: NotificationType) {
    switch (type) {
        case "NEW_BOOKING":
        case "BOOKING_ACCEPTED":
        case "BOOKING_REJECTED":
        case "PASSENGER_CANCELLED":
            return <Ticket className="w-5 h-5" />;
        case "RIDE_STARTED":
        case "RIDE_COMPLETED":
        case "RIDE_CANCELLED":
            return <Car className="w-5 h-5" />;
        default:
            return <Bell className="w-5 h-5" />;
    }
}

// Get icon background color based on notification type
function getIconBgColor(type: NotificationType, isRead: boolean) {
    if (isRead) return "bg-slate-100 text-slate-400";

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
        default:
            return "bg-slate-100 text-slate-600";
    }
}

// Get notification type label
function getTypeLabel(type: NotificationType): string {
    switch (type) {
        case "NEW_BOOKING":
            return "Nouvelle réservation";
        case "BOOKING_ACCEPTED":
            return "Réservation acceptée";
        case "BOOKING_REJECTED":
            return "Réservation refusée";
        case "RIDE_STARTED":
            return "Trajet démarré";
        case "RIDE_COMPLETED":
            return "Trajet terminé";
        case "RIDE_CANCELLED":
            return "Trajet annulé";
        case "PASSENGER_CANCELLED":
            return "Passager annulé";
        default:
            return "Notification";
    }
}

// Format date
function formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffMins < 1) return "À l'instant";
    if (diffMins < 60) return `Il y a ${diffMins} min`;
    if (diffHours < 24) return `Il y a ${diffHours}h`;
    if (diffDays < 7) return `Il y a ${diffDays} jour${diffDays > 1 ? "s" : ""}`;

    return date.toLocaleDateString("fr-FR", {
        weekday: "short",
        day: "numeric",
        month: "short",
        year: date.getFullYear() !== now.getFullYear() ? "numeric" : undefined,
    });
}

function NotificationCard({
    notification,
    onMarkAsRead,
}: {
    notification: Notification;
    onMarkAsRead: (id: string) => void;
}) {
    const content = (
        <div
            className={cn(
                "bg-white rounded-2xl border shadow-sm overflow-hidden transition-all hover:shadow-md",
                !notification.isRead
                    ? "border-[#006B8F]/20 ring-1 ring-[#006B8F]/10"
                    : "border-slate-100"
            )}
        >
            <div className="p-5">
                <div className="flex items-start gap-4">
                    {/* Icon */}
                    <div
                        className={cn(
                            "shrink-0 w-12 h-12 rounded-xl flex items-center justify-center",
                            getIconBgColor(notification.type, notification.isRead)
                        )}
                    >
                        {notification.isRead ? (
                            <Check className="w-5 h-5" />
                        ) : (
                            getNotificationIcon(notification.type)
                        )}
                    </div>

                    {/* Content */}
                    <div className="flex-1 min-w-0">
                        <div className="flex items-start justify-between gap-3">
                            <div>
                                {/* Type label */}
                                <span
                                    className={cn(
                                        "inline-block px-2 py-0.5 rounded-full text-xs font-medium mb-2",
                                        !notification.isRead
                                            ? "bg-[#006B8F]/10 text-[#006B8F]"
                                            : "bg-slate-100 text-slate-500"
                                    )}
                                >
                                    {getTypeLabel(notification.type)}
                                </span>

                                {/* Title */}
                                <h3
                                    className={cn(
                                        "text-base mb-1",
                                        !notification.isRead
                                            ? "font-semibold text-slate-900"
                                            : "font-medium text-slate-600"
                                    )}
                                >
                                    {notification.title}
                                </h3>

                                {/* Message */}
                                <p
                                    className={cn(
                                        "text-sm",
                                        !notification.isRead
                                            ? "text-slate-600"
                                            : "text-slate-400"
                                    )}
                                >
                                    {notification.message}
                                </p>
                            </div>

                            {/* Unread dot */}
                            {!notification.isRead && (
                                <div className="shrink-0 w-3 h-3 bg-[#006B8F] rounded-full animate-pulse" />
                            )}
                        </div>

                        {/* Footer */}
                        <div className="flex items-center justify-between mt-3 pt-3 border-t border-slate-100">
                            <span className="text-xs text-slate-400">
                                {formatDate(notification.createdAt)}
                                {notification.isRead && (
                                    <span className="ml-2 text-green-500">✓ Lu</span>
                                )}
                            </span>

                            <div className="flex items-center gap-2">
                                {notification.link && (
                                    <Link href={notification.link}>
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            className="text-xs text-[#006B8F] hover:bg-[#006B8F]/5"
                                        >
                                            Voir détails
                                        </Button>
                                    </Link>
                                )}
                                {!notification.isRead && (
                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        onClick={(e) => {
                                            e.preventDefault();
                                            onMarkAsRead(notification.id);
                                        }}
                                        className="text-xs text-slate-500 hover:text-slate-700"
                                    >
                                        <Check className="w-3 h-3 mr-1" />
                                        Marquer comme lu
                                    </Button>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );

    return content;
}

export default function NotificationsPage() {
    const router = useRouter();
    const { isAuthenticated, isLoading: authLoading } = useAuth();
    const {
        notifications,
        unreadCount,
        markAsRead,
        markAllAsRead,
    } = useNotifications();
    const [filter, setFilter] = useState<"ALL" | "UNREAD" | "READ">("ALL");
    const [isMarkingAll, setIsMarkingAll] = useState(false);

    // Redirect if not authenticated
    useEffect(() => {
        if (!authLoading && !isAuthenticated) {
            router.replace("/login");
        }
    }, [authLoading, isAuthenticated, router]);

    const handleMarkAllAsRead = async () => {
        setIsMarkingAll(true);
        await markAllAsRead();
        setIsMarkingAll(false);
    };

    const filteredNotifications = notifications.filter((n) => {
        if (filter === "UNREAD") return !n.isRead;
        if (filter === "READ") return n.isRead;
        return true;
    });

    // Loading state
    if (authLoading) {
        return (
            <div className="min-h-screen bg-[#F8FAFC]">
                <Navbar />
                <div className="h-16"></div>
                <div className="flex items-center justify-center py-20">
                    <Loader2 className="w-8 h-8 animate-spin text-[#006B8F]" />
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-[#F8FAFC] pb-20 lg:pb-8">
            <Navbar />
            <div className="h-16"></div>

            <main className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Header */}
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
                    <div className="flex items-center gap-3">
                        <Link
                            href="/dashboard"
                            className="p-2 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-lg transition-colors"
                        >
                            <ArrowLeft className="w-5 h-5" />
                        </Link>
                        <div>
                            <h1 className="text-2xl font-bold text-slate-900 font-[family-name:var(--font-poppins)]">
                                Notifications
                            </h1>
                            <p className="text-slate-500 text-sm">
                                {unreadCount > 0
                                    ? `${unreadCount} notification${unreadCount > 1 ? "s" : ""} non lue${unreadCount > 1 ? "s" : ""}`
                                    : "Toutes les notifications lues"}
                            </p>
                        </div>
                    </div>

                    {unreadCount > 0 && (
                        <Button
                            variant="outline"
                            onClick={handleMarkAllAsRead}
                            disabled={isMarkingAll}
                            className="rounded-xl border-[#006B8F] text-[#006B8F] hover:bg-[#006B8F] hover:text-white"
                        >
                            {isMarkingAll ? (
                                <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                            ) : (
                                <CheckCheck className="w-4 h-4 mr-2" />
                            )}
                            Tout marquer comme lu
                        </Button>
                    )}
                </div>

                {/* Filter Tabs */}
                <div className="flex gap-2 mb-6 overflow-x-auto pb-2">
                    {(["ALL", "UNREAD", "READ"] as const).map((status) => (
                        <button
                            key={status}
                            onClick={() => setFilter(status)}
                            className={cn(
                                "px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-all",
                                filter === status
                                    ? "bg-[#006B8F] text-white"
                                    : "bg-white text-slate-600 hover:bg-slate-50 border border-slate-200"
                            )}
                        >
                            {status === "ALL" && "Toutes"}
                            {status === "UNREAD" && `Non lues (${unreadCount})`}
                            {status === "READ" &&
                                `Lues (${notifications.length - unreadCount})`}
                        </button>
                    ))}
                </div>

                {/* Notifications List */}
                {filteredNotifications.length > 0 ? (
                    <div className="space-y-4">
                        {filteredNotifications.map((notification) => (
                            <NotificationCard
                                key={notification.id}
                                notification={notification}
                                onMarkAsRead={markAsRead}
                            />
                        ))}
                    </div>
                ) : (
                    // Empty State
                    <div className="bg-white rounded-2xl border border-slate-100 p-12 text-center">
                        <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4">
                            <Bell className="w-8 h-8 text-slate-400" />
                        </div>
                        <h3 className="text-lg font-medium text-slate-900 mb-2">
                            {filter === "ALL"
                                ? "Aucune notification"
                                : filter === "UNREAD"
                                    ? "Aucune notification non lue"
                                    : "Aucune notification lue"}
                        </h3>
                        <p className="text-slate-500 mb-6">
                            {filter === "ALL"
                                ? "Vous n'avez pas encore reçu de notifications."
                                : filter === "UNREAD"
                                    ? "Toutes vos notifications ont été lues."
                                    : "Vos notifications non lues apparaîtront ici une fois lues."}
                        </p>
                        <Link href="/dashboard">
                            <Button className="rounded-xl bg-[#006B8F] hover:bg-[#005673]">
                                Retour au tableau de bord
                            </Button>
                        </Link>
                    </div>
                )}
            </main>
        </div>
    );
}
