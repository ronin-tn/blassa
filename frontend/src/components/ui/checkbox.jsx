import * as React from "react"
import { Check } from "lucide-react"
import { cn } from "@/lib/utils"

const Checkbox = React.forwardRef(({ className, checked, onCheckedChange, ...props }, ref) => (
    <button
        type="button"
        role="checkbox"
        aria-checked={checked}
        ref={ref}
        onClick={() => onCheckedChange?.(!checked)}
        className={cn(
            "peer h-4 w-4 shrink-0 rounded-sm border border-slate-300 shadow focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-slate-950 disabled:cursor-not-allowed disabled:opacity-50 data-[state=checked]:bg-[#FF7A00] data-[state=checked]:border-[#FF7A00] data-[state=checked]:text-white",
            checked && "bg-[#FF7A00] border-[#FF7A00] text-white",
            className
        )}
        {...props}
    >
        {checked && (
            <div className="flex items-center justify-center text-current">
                <Check className="h-3.5 w-3.5 stroke-[3]" />
            </div>
        )}
    </button>
))
Checkbox.displayName = "Checkbox"

export { Checkbox }
