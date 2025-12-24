/**
 * Client-side API utilities for browser-side data fetching
 * 
 * Use these functions in Client Components ("use client")
 * For Server Components, use the functions in lib/api/client.ts instead.
 */

import { API_URL } from "@/lib/config";

// Error class for API errors
export class ClientApiError extends Error {
    status: number;
    code?: string;
    details?: unknown;

    constructor(message: string, status: number, code?: string, details?: unknown) {
        super(message);
        this.name = "ClientApiError";
        this.status = status;
        this.code = code;
        this.details = details;
    }
}

/**
 * Parse error response from API
 */
async function parseError(response: Response): Promise<ClientApiError> {
    let message = "Une erreur est survenue";
    let code: string | undefined;
    let details: unknown;

    try {
        const data = await response.json();
        message = data.message || data.error || message;
        code = data.code;
        details = data.details;
    } catch {
        // Response is not JSON
        if (response.status === 401) {
            message = "Session expirée. Veuillez vous reconnecter.";
        } else if (response.status === 403) {
            message = "Accès refusé.";
        } else if (response.status === 404) {
            message = "Ressource introuvable.";
        } else if (response.status === 429) {
            message = "Trop de requêtes. Veuillez réessayer plus tard.";
        } else if (response.status >= 500) {
            message = "Erreur serveur. Veuillez réessayer plus tard.";
        }
    }

    return new ClientApiError(message, response.status, code, details);
}

/**
 * Generic client-side fetch wrapper with error handling
 */
export async function clientFetch<T>(
    endpoint: string,
    options?: RequestInit
): Promise<T> {
    const url = endpoint.startsWith("http") ? endpoint : `${API_URL}${endpoint}`;

    const response = await fetch(url, {
        credentials: "include", // Always send cookies
        headers: {
            "Content-Type": "application/json",
            ...options?.headers,
        },
        ...options,
    });

    if (!response.ok) {
        throw await parseError(response);
    }

    // Handle empty responses (204 No Content, etc.)
    if (response.status === 204 || response.headers.get("content-length") === "0") {
        return undefined as T;
    }

    return response.json();
}

/**
 * GET request
 */
export function clientGet<T>(endpoint: string): Promise<T> {
    return clientFetch<T>(endpoint, { method: "GET" });
}

/**
 * POST request
 */
export function clientPost<T>(endpoint: string, data?: unknown): Promise<T> {
    return clientFetch<T>(endpoint, {
        method: "POST",
        body: data ? JSON.stringify(data) : undefined,
    });
}

/**
 * PUT request
 */
export function clientPut<T>(endpoint: string, data?: unknown): Promise<T> {
    return clientFetch<T>(endpoint, {
        method: "PUT",
        body: data ? JSON.stringify(data) : undefined,
    });
}

/**
 * DELETE request
 */
export function clientDelete<T>(endpoint: string): Promise<T> {
    return clientFetch<T>(endpoint, { method: "DELETE" });
}
