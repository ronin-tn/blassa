"use client";

import { useEffect, useState } from "react";
import { X, Flag } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { clientPost } from "@/lib/api/client-api";

interface ReportModalProps {
    isOpen: boolean;
    onClose: () => void;
    reportedUserId?: string;
    rideId?: string;
}

const REASONS = [
    "Comportement inapproprié",
    "Conduite dangereuse",
    "Absence au rendez-vous",
    "Harcèlement",
    "Fraude / Arnaque",
    "Autre",
];

export default function ReportModal({
    isOpen,
    onClose,
    reportedUserId,
    rideId,
}: ReportModalProps) {
    const [isVisible, setIsVisible] = useState(false);
    const [reason, setReason] = useState("");
    const [description, setDescription] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState(false);

    useEffect(() => {
        if (isOpen) {
            const timer = setTimeout(() => setIsVisible(true), 10);
            document.body.style.overflow = "hidden";
            return () => clearTimeout(timer);
        } else {
            const timer = setTimeout(() => setIsVisible(false), 300);
            document.body.style.overflow = "unset";
            // Reset form only after animation
            const resetTimer = setTimeout(() => {
                setReason("");
                setDescription("");
                setError("");
                setSuccess(false);
            }, 300);
            return () => {
                clearTimeout(timer);
                clearTimeout(resetTimer);
            };
        }
    }, [isOpen]);

    const handleSubmit = async () => {
        if (!reason || !description) {
            setError("Veuillez remplir tous les champs");
            return;
        }

        setIsLoading(true);
        setError("");

        try {
            await clientPost("/reports", {
                reportedUserId: reportedUserId || null,
                rideId: rideId || null,
                reason,
                description,
            });
            setSuccess(true);
            setTimeout(() => {
                onClose();
            }, 1500);
        } catch (err: any) {
            setError(err.message || "Une erreur est survenue");
        } finally {
            setIsLoading(false);
        }
    };

    if (!isVisible) return null;

    return (
        <div className={`fixed inset-0 z-50 flex items-center justify-center p-4 transition-opacity duration-300 ${isOpen ? "opacity-100" : "opacity-0"}`}>
            <div className="absolute inset-0 bg-black/40 backdrop-blur-sm" onClick={onClose} />
            <div className={`relative bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden transform transition-all duration-300 ${isOpen ? "scale-100 translate-y-0" : "scale-95 translate-y-4"}`}>
                <div className="p-6">
                    <div className="flex items-start justify-between mb-4">
                        <div className="p-3 rounded-full bg-amber-50 text-amber-600">
                            <Flag className="w-6 h-6" />
                        </div>
                        <button onClick={onClose} disabled={isLoading} className="text-slate-400 hover:text-slate-600 transition-colors p-1">
                            <X className="w-5 h-5" />
                        </button>
                    </div>

                    <h3 className="text-xl font-bold text-slate-900 mb-2">Signaler</h3>

                    {success ? (
                        <div className="text-center py-8">
                            <p className="text-emerald-600 font-medium">Signalement envoyé avec succès.</p>
                        </div>
                    ) : (
                        <>
                            <p className="text-slate-600 mb-6 text-sm">
                                Votre signalement sera traité de manière confidentielle par notre équipe de modération.
                            </p>

                            <div className="space-y-4">
                                <div className="space-y-2">
                                    <label className="text-sm font-medium text-slate-700">Raison</label>
                                    <Select value={reason} onValueChange={setReason}>
                                        <SelectTrigger className="w-full rounded-xl">
                                            <SelectValue placeholder="Sélectionnez une raison" />
                                        </SelectTrigger>
                                        <SelectContent className="z-[60]">
                                            {REASONS.map((r) => (
                                                <SelectItem key={r} value={r}>{r}</SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                </div>

                                <div className="space-y-2">
                                    <label className="text-sm font-medium text-slate-700">Description</label>
                                    <textarea
                                        value={description}
                                        onChange={(e) => setDescription(e.target.value)}
                                        className="w-full min-h-[100px] p-3 rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-[#006B8F]"
                                        placeholder="Décrivez le problème en détail..."
                                    />
                                </div>

                                {error && (
                                    <p className="text-sm text-red-500">{error}</p>
                                )}

                                <div className="flex gap-3 justify-end pt-2">
                                    <Button variant="outline" onClick={onClose} disabled={isLoading} className="rounded-xl">
                                        Annuler
                                    </Button>
                                    <Button onClick={handleSubmit} disabled={isLoading} className="bg-[#006B8F] hover:bg-[#005673] rounded-xl text-white">
                                        {isLoading ? "Envoi..." : "Signaler"}
                                    </Button>
                                </div>
                            </div>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}
