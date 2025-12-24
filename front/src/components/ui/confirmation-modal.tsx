"use client";

import { useEffect, useState } from "react";
import { X, AlertTriangle } from "lucide-react";
import { Button } from "@/components/ui/button";

interface ConfirmationModalProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: () => void;
    title: string;
    message: string;
    confirmLabel?: string;
    cancelLabel?: string;
    isLoading?: boolean;
    isDestructive?: boolean;
}

export default function ConfirmationModal({
    isOpen,
    onClose,
    onConfirm,
    title,
    message,
    confirmLabel = "Confirmer",
    cancelLabel = "Annuler",
    isLoading = false,
    isDestructive = false,
}: ConfirmationModalProps) {
    const [isVisible, setIsVisible] = useState(false);

    useEffect(() => {
        if (isOpen) {
            setIsVisible(true);
            // Prevent scrolling when modal is open
            document.body.style.overflow = "hidden";
        } else {
            const timer = setTimeout(() => setIsVisible(false), 300); // Wait for animation
            document.body.style.overflow = "unset";
            return () => clearTimeout(timer);
        }
    }, [isOpen]);

    if (!isVisible) return null;

    return (
        <div
            className={`fixed inset-0 z-50 flex items-center justify-center p-4 transition-opacity duration-300 ${isOpen ? "opacity-100" : "opacity-0"
                }`}
        >
            {/* Backdrop */}
            <div
                className="absolute inset-0 bg-black/40 backdrop-blur-sm"
                onClick={isLoading ? undefined : onClose}
            />

            {/* Modal Content */}
            <div
                className={`relative bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden transform transition-all duration-300 ${isOpen ? "scale-100 translate-y-0" : "scale-95 translate-y-4"
                    }`}
            >
                <div className="p-6">
                    <div className="flex items-start justify-between mb-4">
                        <div className={`p-3 rounded-full ${isDestructive ? "bg-red-50 text-red-600" : "bg-slate-100 text-slate-600"}`}>
                            <AlertTriangle className="w-6 h-6" />
                        </div>
                        <button
                            onClick={onClose}
                            disabled={isLoading}
                            className="text-slate-400 hover:text-slate-600 transition-colors p-1"
                        >
                            <X className="w-5 h-5" />
                        </button>
                    </div>

                    <h3 className="text-xl font-bold text-slate-900 mb-2">
                        {title}
                    </h3>

                    <p className="text-slate-600 mb-6 leading-relaxed">
                        {message}
                    </p>

                    <div className="flex gap-3 justify-end">
                        <Button
                            variant="outline"
                            onClick={onClose}
                            disabled={isLoading}
                            className="rounded-xl border-slate-200 text-slate-700 hover:bg-slate-50 hover:text-slate-900"
                        >
                            {cancelLabel}
                        </Button>
                        <Button
                            onClick={onConfirm}
                            disabled={isLoading}
                            className={`rounded-xl text-white ${isDestructive
                                ? "bg-red-600 hover:bg-red-700 border-red-600"
                                : "bg-[#006B8F] hover:bg-[#005673] border-[#006B8F]"
                                }`}
                        >
                            {isLoading ? (
                                <div className="flex items-center gap-2">
                                    <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                                    <span>Traitement...</span>
                                </div>
                            ) : (
                                confirmLabel
                            )}
                        </Button>
                    </div>
                </div>
            </div>
        </div>
    );
}
