"use client";

import { useState, useEffect, useCallback } from "react";

const STORAGE_KEY = "blassa_recent_searches";
const MAX_SEARCHES = 5;

export interface RecentSearch {
    id: string;
    from: string;
    to: string;
    date?: string;
    passengers: number;
    timestamp: number;
}

/**
 * Hook to manage recent searches in localStorage
 * - Maximum 5 recent searches stored
 * - Deduplicates based on from/to combination
 * - Sorted by most recent first
 */
export function useRecentSearches() {
    const [searches, setSearches] = useState<RecentSearch[]>([]);
    const [isLoaded, setIsLoaded] = useState(false);

    // Load from localStorage on mount
    useEffect(() => {
        try {
            const stored = localStorage.getItem(STORAGE_KEY);
            if (stored) {
                const parsed = JSON.parse(stored) as RecentSearch[];
                // Validate and sanitize data
                const valid = parsed.filter(
                    (s) =>
                        typeof s.from === "string" &&
                        typeof s.to === "string" &&
                        typeof s.timestamp === "number"
                );
                setSearches(valid.slice(0, MAX_SEARCHES));
            }
        } catch {
            // Invalid data, clear it
            localStorage.removeItem(STORAGE_KEY);
        } finally {
            setIsLoaded(true);
        }
    }, []);

    // Save to localStorage
    const persist = useCallback((items: RecentSearch[]) => {
        try {
            localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
        } catch {
            // Storage full or unavailable - fail silently
        }
    }, []);

    // Add a new search
    const addSearch = useCallback(
        (search: Omit<RecentSearch, "id" | "timestamp">) => {
            setSearches((prev) => {
                // Remove duplicate (same from/to)
                const filtered = prev.filter(
                    (s) =>
                        !(
                            s.from.toLowerCase() === search.from.toLowerCase() &&
                            s.to.toLowerCase() === search.to.toLowerCase()
                        )
                );

                const newSearch: RecentSearch = {
                    ...search,
                    id: `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`,
                    timestamp: Date.now(),
                };

                const updated = [newSearch, ...filtered].slice(0, MAX_SEARCHES);
                persist(updated);
                return updated;
            });
        },
        [persist]
    );

    // Clear all searches
    const clearSearches = useCallback(() => {
        setSearches([]);
        localStorage.removeItem(STORAGE_KEY);
    }, []);

    // Remove a single search
    const removeSearch = useCallback(
        (id: string) => {
            setSearches((prev) => {
                const updated = prev.filter((s) => s.id !== id);
                persist(updated);
                return updated;
            });
        },
        [persist]
    );

    return {
        searches,
        isLoaded,
        addSearch,
        removeSearch,
        clearSearches,
    };
}
