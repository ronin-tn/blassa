"use client";

import { Star } from "lucide-react";
import { useState } from "react";

interface StarRatingInputProps {
    rating: number;
    onChange: (rating: number) => void;
    disabled?: boolean;
    size?: "sm" | "md" | "lg";
}

export default function StarRatingInput({
    rating,
    onChange,
    disabled = false,
    size = "md"
}: StarRatingInputProps) {
    const [hoverRating, setHoverRating] = useState<number | null>(null);

    const stars = [1, 2, 3, 4, 5];

    const sizeClasses = {
        sm: "w-5 h-5",
        md: "w-8 h-8",
        lg: "w-10 h-10"
    };

    return (
        <div className="flex gap-1">
            {stars.map((star) => (
                <button
                    key={star}
                    type="button"
                    disabled={disabled}
                    onClick={() => onChange(star)}
                    onMouseEnter={() => !disabled && setHoverRating(star)}
                    onMouseLeave={() => !disabled && setHoverRating(null)}
                    className={`transition-transform ${!disabled && "hover:scale-110 focus:outline-none"}`}
                >
                    <Star
                        className={`${sizeClasses[size]} transition-colors ${(hoverRating !== null ? star <= hoverRating : star <= rating)
                                ? "fill-yellow-400 text-yellow-400"
                                : "fill-slate-100 text-slate-300"
                            }`}
                    />
                </button>
            ))}
        </div>
    );
}
