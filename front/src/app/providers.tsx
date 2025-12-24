"use client";

import { ReactNode } from "react";
import { AuthProvider } from "@/contexts/AuthContext";
import { NotificationProvider } from "@/contexts/NotificationContext";
import { ToastProvider } from "@/contexts/ToastContext";

export function Providers({ children }: { children: ReactNode }) {
    return (
        <AuthProvider>
            <NotificationProvider>
                <ToastProvider>{children}</ToastProvider>
            </NotificationProvider>
        </AuthProvider>
    );
}
