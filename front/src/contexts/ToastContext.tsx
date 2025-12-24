"use client";

import {
    createContext,
    useContext,
    useState,
    useCallback,
    ReactNode,
} from "react";

// ============================================================================
// Types
// ============================================================================

export type ToastType = "success" | "error" | "info" | "warning";

export interface Toast {
    id: string;
    message: string;
    type: ToastType;
}

interface ToastContextType {
    toasts: Toast[];
    showToast: (message: string, type?: ToastType) => void;
    showSuccess: (message: string) => void;
    showError: (message: string) => void;
    dismissToast: (id: string) => void;
}

// ============================================================================
// Context
// ============================================================================

const ToastContext = createContext<ToastContextType | undefined>(undefined);

// ============================================================================
// Provider
// ============================================================================

export function ToastProvider({ children }: { children: ReactNode }) {
    const [toasts, setToasts] = useState<Toast[]>([]);

    const showToast = useCallback((message: string, type: ToastType = "info") => {
        const id = `toast-${Date.now()}-${Math.random().toString(36).slice(2)}`;
        const newToast: Toast = { id, message, type };

        setToasts((prev) => [...prev, newToast]);

        // Auto-dismiss after 5 seconds
        setTimeout(() => {
            setToasts((prev) => prev.filter((t) => t.id !== id));
        }, 5000);
    }, []);

    const showSuccess = useCallback((message: string) => {
        showToast(message, "success");
    }, [showToast]);

    const showError = useCallback((message: string) => {
        showToast(message, "error");
    }, [showToast]);

    const dismissToast = useCallback((id: string) => {
        setToasts((prev) => prev.filter((t) => t.id !== id));
    }, []);

    return (
        <ToastContext.Provider value={{ toasts, showToast, showSuccess, showError, dismissToast }}>
            {children}
            <ToastContainer toasts={toasts} onDismiss={dismissToast} />
        </ToastContext.Provider>
    );
}

// ============================================================================
// Hook
// ============================================================================

export function useToast() {
    const context = useContext(ToastContext);
    if (context === undefined) {
        throw new Error("useToast must be used within a ToastProvider");
    }
    return context;
}

// ============================================================================
// Toast Container Component
// ============================================================================

const toastStyles: Record<ToastType, string> = {
    success: "bg-emerald-600 text-white",
    error: "bg-red-600 text-white",
    warning: "bg-amber-500 text-white",
    info: "bg-blue-600 text-white",
};

function ToastContainer({
    toasts,
    onDismiss,
}: {
    toasts: Toast[];
    onDismiss: (id: string) => void;
}) {
    if (toasts.length === 0) return null;

    return (
        <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-2 max-w-sm">
            {toasts.map((toast) => (
                <div
                    key={toast.id}
                    className={`px-4 py-3 rounded-xl shadow-lg flex items-center justify-between gap-3 animate-in slide-in-from-right-full duration-300 ${toastStyles[toast.type]}`}
                    role="alert"
                >
                    <p className="text-sm font-medium">{toast.message}</p>
                    <button
                        onClick={() => onDismiss(toast.id)}
                        className="shrink-0 opacity-70 hover:opacity-100 transition-opacity"
                        aria-label="Fermer"
                    >
                        <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>
            ))}
        </div>
    );
}
