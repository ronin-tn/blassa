/*
  Input Wrapper Component
  -----------------------
  Wraps shadcn's Input with Label and error handling.
  This gives us the best of both worlds:
  - Shadcn's accessibility and consistent styling
  - Our convenient label + error pattern
*/

import { Input as ShadcnInput } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { cn } from "@/lib/utils";

const Input = ({
    label,
    error,
    icon,
    className,
    id,
    ...props
}) => {
    // Generate a random ID if not provided (needed for label htmlFor)
    const inputId = id || `input-${Math.random().toString(36).substr(2, 9)}`;

    return (
        <div className={cn("w-full space-y-2", className)}>
            {/* Label using shadcn's Label component */}
            {label && (
                <Label
                    htmlFor={inputId}
                    className={cn(error && "text-destructive")}
                >
                    {label}
                </Label>
            )}

            <div className="relative">
                {/* Optional icon */}
                {icon && (
                    <div className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground pointer-events-none">
                        {icon}
                    </div>
                )}

                {/* shadcn Input with error styling */}
                <ShadcnInput
                    id={inputId}
                    className={cn(
                        icon && "pl-9",
                        error && "border-destructive focus-visible:ring-destructive"
                    )}
                    aria-invalid={error ? "true" : undefined}
                    aria-describedby={error ? `${inputId}-error` : undefined}
                    {...props}
                />
            </div>

            {/* Error message */}
            {error && (
                <p
                    id={`${inputId}-error`}
                    className="text-sm font-medium text-destructive"
                >
                    {error}
                </p>
            )}
        </div>
    );
};

export default Input;
