"use client";

import { useState, useRef, useCallback } from "react";
import Image from "next/image";
import { Camera, Upload, X, Loader2, Trash2 } from "lucide-react";
import { useToast } from "@/contexts/ToastContext";

interface ProfilePictureUploadProps {
    currentImageUrl: string | null;
    initials: string;
    onUploadSuccess: () => void;
}

/**
 * Profile picture upload component with drag-and-drop support.
 * Handles image preview, upload progress, and error states.
 */
export default function ProfilePictureUpload({
    currentImageUrl,
    initials,
    onUploadSuccess,
}: ProfilePictureUploadProps) {
    const [isUploading, setIsUploading] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);
    const [previewUrl, setPreviewUrl] = useState<string | null>(null);
    const [isDragging, setIsDragging] = useState(false);
    const [showOptions, setShowOptions] = useState(false);
    const fileInputRef = useRef<HTMLInputElement>(null);
    const { showSuccess, showError } = useToast();

    const handleFileSelect = useCallback(async (file: File) => {
        // Validate file type
        const allowedTypes = ["image/jpeg", "image/png", "image/webp", "image/gif"];
        if (!allowedTypes.includes(file.type)) {
            showError("Type de fichier non supporté. Utilisez JPEG, PNG, WebP ou GIF");
            return;
        }

        // Validate file size (5MB max)
        if (file.size > 5 * 1024 * 1024) {
            showError("Le fichier est trop volumineux (max 5 Mo)");
            return;
        }

        // Create preview
        const reader = new FileReader();
        reader.onload = (e) => {
            setPreviewUrl(e.target?.result as string);
        };
        reader.readAsDataURL(file);

        // Upload file
        setIsUploading(true);
        try {
            const formData = new FormData();
            formData.append("file", file);

            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/user/me/picture`,
                {
                    method: "POST",
                    credentials: "include",
                    body: formData,
                }
            );

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || "Erreur lors du téléchargement");
            }

            showSuccess("Photo de profil mise à jour !");
            onUploadSuccess();
            setPreviewUrl(null);
        } catch (error) {
            showError(error instanceof Error ? error.message : "Erreur lors du téléchargement");
            setPreviewUrl(null);
        } finally {
            setIsUploading(false);
        }
    }, [onUploadSuccess, showError, showSuccess]);

    const handleDelete = async () => {
        if (!currentImageUrl) return;

        setIsDeleting(true);
        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/user/me/picture`,
                {
                    method: "DELETE",
                    credentials: "include",
                }
            );

            if (!response.ok) {
                throw new Error("Erreur lors de la suppression");
            }

            showSuccess("Photo de profil supprimée");
            onUploadSuccess();
            setShowOptions(false);
        } catch (error) {
            showError(error instanceof Error ? error.message : "Erreur lors de la suppression");
        } finally {
            setIsDeleting(false);
        }
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
            handleFileSelect(file);
        }
    };

    const handleDrop = useCallback((e: React.DragEvent) => {
        e.preventDefault();
        setIsDragging(false);
        const file = e.dataTransfer.files?.[0];
        if (file) {
            handleFileSelect(file);
        }
    }, [handleFileSelect]);

    const handleDragOver = (e: React.DragEvent) => {
        e.preventDefault();
        setIsDragging(true);
    };

    const handleDragLeave = (e: React.DragEvent) => {
        e.preventDefault();
        setIsDragging(false);
    };

    const displayUrl = previewUrl || currentImageUrl;

    return (
        <div className="relative inline-block">
            {/* Main Avatar Container */}
            <div
                className={`relative w-24 h-24 rounded-full cursor-pointer transition-all duration-200 ${isDragging
                        ? "ring-4 ring-white/50 ring-offset-2 ring-offset-transparent scale-105"
                        : "hover:scale-105"
                    }`}
                onClick={() => !isUploading && setShowOptions(!showOptions)}
                onDrop={handleDrop}
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
            >
                {/* Avatar Image or Initials */}
                <div className="w-full h-full rounded-full bg-white/20 backdrop-blur flex items-center justify-center text-white text-3xl font-bold border-4 border-white/30 overflow-hidden">
                    {displayUrl ? (
                        <Image
                            src={displayUrl}
                            alt="Photo de profil"
                            fill
                            className="object-cover"
                            sizes="96px"
                        />
                    ) : (
                        initials
                    )}
                </div>

                {/* Loading Overlay */}
                {(isUploading || isDeleting) && (
                    <div className="absolute inset-0 bg-black/50 rounded-full flex items-center justify-center">
                        <Loader2 className="w-8 h-8 text-white animate-spin" />
                    </div>
                )}

                {/* Camera Icon Overlay */}
                {!isUploading && !isDeleting && (
                    <div className="absolute bottom-0 right-0 w-8 h-8 bg-white rounded-full shadow-lg flex items-center justify-center border-2 border-[#0A8F8F]">
                        <Camera className="w-4 h-4 text-[#0A8F8F]" />
                    </div>
                )}

                {/* Drag Overlay */}
                {isDragging && (
                    <div className="absolute inset-0 bg-white/30 rounded-full flex items-center justify-center backdrop-blur-sm">
                        <Upload className="w-8 h-8 text-white" />
                    </div>
                )}
            </div>

            {/* Options Dropdown */}
            {showOptions && !isUploading && !isDeleting && (
                <div className="absolute top-full left-1/2 -translate-x-1/2 mt-2 bg-white rounded-xl shadow-xl border border-gray-100 overflow-hidden z-50 min-w-[160px]">
                    <button
                        onClick={(e) => {
                            e.stopPropagation();
                            fileInputRef.current?.click();
                            setShowOptions(false);
                        }}
                        className="w-full flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                    >
                        <Upload className="w-4 h-4 text-[#0A8F8F]" />
                        <span>Changer la photo</span>
                    </button>
                    {currentImageUrl && (
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                handleDelete();
                            }}
                            className="w-full flex items-center gap-3 px-4 py-3 text-sm text-red-600 hover:bg-red-50 transition-colors border-t border-gray-100"
                        >
                            <Trash2 className="w-4 h-4" />
                            <span>Supprimer</span>
                        </button>
                    )}
                    <button
                        onClick={(e) => {
                            e.stopPropagation();
                            setShowOptions(false);
                        }}
                        className="w-full flex items-center gap-3 px-4 py-3 text-sm text-gray-500 hover:bg-gray-50 transition-colors border-t border-gray-100"
                    >
                        <X className="w-4 h-4" />
                        <span>Annuler</span>
                    </button>
                </div>
            )}

            {/* Hidden File Input */}
            <input
                ref={fileInputRef}
                type="file"
                accept="image/jpeg,image/png,image/webp,image/gif"
                onChange={handleInputChange}
                className="hidden"
            />

            {/* Click outside to close */}
            {showOptions && (
                <div
                    className="fixed inset-0 z-40"
                    onClick={() => setShowOptions(false)}
                />
            )}
        </div>
    );
}
