"use client";

import { useState } from "react";
import { MapPin, ChevronDown, Check } from "lucide-react";
import { TUNISIA_CITIES, City } from "@/data/cities";
import { cn } from "@/lib/utils";

interface CitySelectProps {
    label: string;
    placeholder?: string;
    value: City | null;
    onChange: (city: City | null) => void;
    excludeCity?: string;
    className?: string;
}

export default function CitySelect({
    label,
    placeholder = "Sélectionner une ville",
    value,
    onChange,
    excludeCity,
    className,
}: CitySelectProps) {
    const [isOpen, setIsOpen] = useState(false);
    const [search, setSearch] = useState("");

    const filteredCities = TUNISIA_CITIES.filter((city) => {
        if (excludeCity && city.code === excludeCity) return false;
        if (!search) return true;
        return city.name.toLowerCase().includes(search.toLowerCase());
    });

    const handleSelect = (city: City) => {
        onChange(city);
        setIsOpen(false);
        setSearch("");
    };

    return (
        <div className={cn("relative w-full space-y-2", className)}>
            <label className="text-sm font-medium text-slate-700">{label}</label>

            <button
                type="button"
                onClick={() => setIsOpen(!isOpen)}
                className="w-full flex items-center justify-between px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-left hover:border-orange-300 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-colors"
            >
                <div className="flex items-center gap-2">
                    <MapPin className="w-4 h-4 text-slate-400" />
                    <span className={value ? "text-slate-900" : "text-slate-400"}>
                        {value?.name || placeholder}
                    </span>
                </div>
                <ChevronDown
                    className={cn(
                        "w-4 h-4 text-slate-400 transition-transform",
                        isOpen && "rotate-180"
                    )}
                />
            </button>

            {isOpen && (
                <>
                    <div
                        className="fixed inset-0 z-40"
                        onClick={() => setIsOpen(false)}
                    />
                    <div className="absolute z-50 w-full mt-1 bg-white border border-slate-200 rounded-xl shadow-lg max-h-64 overflow-hidden">
                        {/* Search Input */}
                        <div className="p-2 border-b border-slate-100">
                            <input
                                type="text"
                                placeholder="Rechercher..."
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}
                                className="w-full px-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                                autoFocus
                            />
                        </div>

                        {/* City List */}
                        <div className="max-h-48 overflow-y-auto">
                            {filteredCities.length === 0 ? (
                                <div className="px-4 py-3 text-sm text-slate-500">
                                    Aucune ville trouvée
                                </div>
                            ) : (
                                filteredCities.map((city) => (
                                    <button
                                        key={city.code}
                                        type="button"
                                        onClick={() => handleSelect(city)}
                                        className={cn(
                                            "w-full flex items-center justify-between px-4 py-2.5 text-left hover:bg-orange-50 transition-colors",
                                            value?.code === city.code && "bg-orange-50 text-orange-700"
                                        )}
                                    >
                                        <div className="flex items-center gap-2">
                                            <MapPin className="w-4 h-4 text-slate-400" />
                                            <span className="font-medium">{city.name}</span>
                                        </div>
                                        {value?.code === city.code && (
                                            <Check className="w-4 h-4 text-orange-600" />
                                        )}
                                    </button>
                                ))
                            )}
                        </div>
                    </div>
                </>
            )}
        </div>
    );
}
