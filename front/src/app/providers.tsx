"use client";

import { ReactNode } from "react";
import { AuthProvider } from "@/contexts/AuthContext";
import { NotificationProvider } from "@/contexts/NotificationContext";

export function Providers({ children }: { children: ReactNode }) {
    return (
        <AuthProvider>
            <NotificationProvider>{children}</NotificationProvider>
        </AuthProvider>
    );
}

