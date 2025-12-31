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


export function useRecentSearches() {
    const [searches, setSearches] = useState<RecentSearch[]>([]);
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        try {
            const stored = localStorage.getItem(STORAGE_KEY);
            if (stored) {
                const parsed = JSON.parse(stored) as RecentSearch[];
                const valid = parsed.filter(
                    (s) =>
                        typeof s.from === "string" &&
                        typeof s.to === "string" &&
                        typeof s.timestamp === "number"
                );
                setSearches(valid.slice(0, MAX_SEARCHES));
            }
        } catch {
            localStorage.removeItem(STORAGE_KEY);
        } finally {
            setIsLoaded(true);
        }
    }, []);

    const persist = useCallback((items: RecentSearch[]) => {
        try {
            localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
        } catch {
        }
    }, []);

    const addSearch = useCallback(
        (search: Omit<RecentSearch, "id" | "timestamp">) => {
            setSearches((prev) => {
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

    const clearSearches = useCallback(() => {
        setSearches([]);
        localStorage.removeItem(STORAGE_KEY);
    }, []);
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
