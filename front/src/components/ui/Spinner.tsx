import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

interface SpinnerProps {
    className?: string;
    size?: "sm" | "md" | "lg";
}

export function Spinner({ className, size = "md" }: SpinnerProps) {
    const sizeClasses = {
        sm: "h-4 w-4 border-2",
        md: "h-8 w-8 border-4",
        lg: "h-12 w-12 border-4",
    };

    return (
        <div
            className={cn(
                "animate-spin rounded-full border-gray-200 border-t-[#0e7490]", // Brand Teal
                sizeClasses[size],
                className
            )}
        />
    );
}
