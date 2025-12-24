"use client";

import { useState } from "react";
import {
    SlidersHorizontal,
    Clock,
    Banknote,
    Users,
    Cigarette,
    ArrowUpDown,
    X,
    RotateCcw,
} from "lucide-react";

export interface SearchFilters {
    sortBy: "price_asc" | "price_desc" | "time_asc" | "time_desc" | "";
    timeOfDay: ("morning" | "afternoon" | "evening")[];
    maxPrice: number | null;
    ladiesOnly: boolean;
    noSmoking: boolean;
}

interface SearchFiltersProps {
    filters: SearchFilters;
    onFiltersChange: (filters: SearchFilters) => void;
    maxPriceInResults: number;
    onClose?: () => void;
    isMobile?: boolean;
}

const TIME_OPTIONS = [
    { id: "morning" as const, label: "Matin", range: "6h - 12h" },
    { id: "afternoon" as const, label: "Après-midi", range: "12h - 18h" },
    { id: "evening" as const, label: "Soir", range: "18h - 00h" },
];

const SORT_OPTIONS = [

    { value: "price_asc", label: "Prix croissant" },
    { value: "price_desc", label: "Prix décroissant" },
    { value: "time_asc", label: "Départ le plus tôt" },
    { value: "time_desc", label: "Départ le plus tard" },
];

export const DEFAULT_FILTERS: SearchFilters = {
    sortBy: "",
    timeOfDay: [],
    maxPrice: null,
    ladiesOnly: false,
    noSmoking: false,
};

export default function SearchFiltersComponent({
    filters,
    onFiltersChange,
    maxPriceInResults,
    onClose,
    isMobile = false,
}: SearchFiltersProps) {
    const [localMaxPrice, setLocalMaxPrice] = useState<string>(
        filters.maxPrice?.toString() || ""
    );

    const updateFilter = <K extends keyof SearchFilters>(
        key: K,
        value: SearchFilters[K]
    ) => {
        onFiltersChange({ ...filters, [key]: value });
    };

    const toggleTimeOfDay = (time: "morning" | "afternoon" | "evening") => {
        const current = filters.timeOfDay;
        const updated = current.includes(time)
            ? current.filter((t) => t !== time)
            : [...current, time];
        updateFilter("timeOfDay", updated);
    };

    const handleMaxPriceChange = (value: string) => {
        setLocalMaxPrice(value);
        const numValue = parseInt(value);
        updateFilter("maxPrice", isNaN(numValue) ? null : numValue);
    };

    const resetFilters = () => {
        setLocalMaxPrice("");
        onFiltersChange(DEFAULT_FILTERS);
    };

    const hasActiveFilters =
        filters.sortBy !== "" ||
        filters.timeOfDay.length > 0 ||
        filters.maxPrice !== null ||
        filters.ladiesOnly ||
        filters.noSmoking;

    const FilterContent = (
        <div className="space-y-6">
            {/* Sort */}
            <div>
                <label className="flex items-center gap-2 text-sm font-semibold text-slate-900 mb-3">
                    <ArrowUpDown className="w-4 h-4 text-slate-500" />
                    Trier par
                </label>
                <select
                    value={filters.sortBy}
                    onChange={(e) =>
                        updateFilter("sortBy", e.target.value as SearchFilters["sortBy"])
                    }
                    className="w-full px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-[#0A8F8F] focus:border-transparent"
                >
                    {SORT_OPTIONS.map((opt) => (
                        <option key={opt.value} value={opt.value}>
                            {opt.label}
                        </option>
                    ))}
                </select>
            </div>

            {/* Time of Day */}
            <div>
                <label className="flex items-center gap-2 text-sm font-semibold text-slate-900 mb-3">
                    <Clock className="w-4 h-4 text-slate-500" />
                    Heure de départ
                </label>
                <div className="space-y-2">
                    {TIME_OPTIONS.map((time) => (
                        <label
                            key={time.id}
                            className="flex items-center gap-3 p-2.5 rounded-lg hover:bg-slate-50 cursor-pointer transition-colors"
                        >
                            <input
                                type="checkbox"
                                checked={filters.timeOfDay.includes(time.id)}
                                onChange={() => toggleTimeOfDay(time.id)}
                                className="w-4 h-4 rounded border-slate-300 text-[#0A8F8F] focus:ring-[#0A8F8F]"
                            />
                            <div className="flex-1">
                                <span className="text-sm font-medium text-slate-700">
                                    {time.label}
                                </span>
                                <span className="text-xs text-slate-400 ml-2">
                                    {time.range}
                                </span>
                            </div>
                        </label>
                    ))}
                </div>
            </div>

            {/* Max Price */}
            <div>
                <label className="flex items-center gap-2 text-sm font-semibold text-slate-900 mb-3">
                    <Banknote className="w-4 h-4 text-slate-500" />
                    Prix maximum
                </label>
                <div className="flex items-center gap-2">
                    <input
                        type="number"
                        min="0"
                        max={maxPriceInResults}
                        value={localMaxPrice}
                        onChange={(e) => handleMaxPriceChange(e.target.value)}
                        placeholder={`Max ${maxPriceInResults} TND`}
                        className="flex-1 px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-[#0A8F8F] focus:border-transparent"
                    />
                    <span className="text-sm text-slate-500">TND</span>
                </div>
            </div>

            {/* Preferences */}
            <div>
                <label className="flex items-center gap-2 text-sm font-semibold text-slate-900 mb-3">
                    <SlidersHorizontal className="w-4 h-4 text-slate-500" />
                    Préférences
                </label>
                <div className="space-y-2">
                    <label className="flex items-center gap-3 p-2.5 rounded-lg hover:bg-slate-50 cursor-pointer transition-colors">
                        <input
                            type="checkbox"
                            checked={filters.ladiesOnly}
                            onChange={(e) => updateFilter("ladiesOnly", e.target.checked)}
                            className="w-4 h-4 rounded border-slate-300 text-pink-500 focus:ring-pink-500"
                        />
                        <div className="flex items-center gap-2">
                            <Users className="w-4 h-4 text-pink-500" />
                            <span className="text-sm font-medium text-slate-700">
                                Ladies Only
                            </span>
                        </div>
                    </label>
                    <label className="flex items-center gap-3 p-2.5 rounded-lg hover:bg-slate-50 cursor-pointer transition-colors">
                        <input
                            type="checkbox"
                            checked={filters.noSmoking}
                            onChange={(e) => updateFilter("noSmoking", e.target.checked)}
                            className="w-4 h-4 rounded border-slate-300 text-[#0A8F8F] focus:ring-[#0A8F8F]"
                        />
                        <div className="flex items-center gap-2">
                            <Cigarette className="w-4 h-4 text-slate-500" />
                            <span className="text-sm font-medium text-slate-700">
                                Non-fumeur
                            </span>
                        </div>
                    </label>
                </div>
            </div>

            {/* Reset Button */}
            {hasActiveFilters && (
                <button
                    onClick={resetFilters}
                    className="w-full flex items-center justify-center gap-2 px-4 py-2.5 text-sm font-medium text-slate-600 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 rounded-xl transition-colors"
                >
                    <RotateCcw className="w-4 h-4" />
                    Réinitialiser les filtres
                </button>
            )}
        </div>
    );

    if (isMobile) {
        return (
            <div className="fixed inset-0 bg-black/50 z-50 flex items-end">
                <div className="bg-white w-full rounded-t-2xl max-h-[80vh] overflow-y-auto animate-slide-up">
                    <div className="sticky top-0 bg-white border-b border-slate-100 px-4 py-3 flex items-center justify-between">
                        <h2 className="text-lg font-semibold text-slate-900">Filtres</h2>
                        <button
                            onClick={onClose}
                            className="p-2 hover:bg-slate-100 rounded-lg"
                        >
                            <X className="w-5 h-5 text-slate-500" />
                        </button>
                    </div>
                    <div className="p-4">{FilterContent}</div>
                    <div className="sticky bottom-0 bg-white border-t border-slate-100 p-4">
                        <button
                            onClick={onClose}
                            className="w-full py-3 bg-[#0A8F8F] text-white font-semibold rounded-xl hover:bg-[#0A8F8F]/90 transition-colors"
                        >
                            Appliquer
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="bg-white rounded-2xl border border-slate-200 p-5 sticky top-20">
            <h2 className="text-lg font-semibold text-slate-900 mb-5 flex items-center gap-2">
                <SlidersHorizontal className="w-5 h-5 text-slate-500" />
                Filtres
            </h2>
            {FilterContent}
        </div>
    );
}
