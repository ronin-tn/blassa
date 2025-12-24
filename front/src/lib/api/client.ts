/**
 * API Client for server-side data fetching
 * This client forwards cookies for authentication and handles common error scenarios
 */

import { cookies } from "next/headers";
import { API_URL, AUTH_COOKIE_NAME } from "@/lib/config";

export interface ApiError {
    status: number;
    message: string;
    code?: string;
}

export class ApiClientError extends Error {
    status: number;
    code?: string;

    constructor(message: string, status: number, code?: string) {
        super(message);
        this.name = "ApiClientError";
        this.status = status;
        this.code = code;
    }
}

/**
 * Server-side fetch wrapper that forwards authentication cookies
 */
export async function apiClient<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T> {
    const cookieStore = await cookies();
    const token = cookieStore.get(AUTH_COOKIE_NAME)?.value;

    const headers: HeadersInit = {
        "Content-Type": "application/json",
        ...options.headers,
    };

    // Forward the auth cookie as a header for server-side requests
    if (token) {
        (headers as Record<string, string>)["Authorization"] = `Bearer ${token}`;
    }

    const url = endpoint.startsWith("http") ? endpoint : `${API_URL}${endpoint}`;

    const response = await fetch(url, {
        ...options,
        headers,
        // Don't cache by default for dynamic data
        cache: options.cache || "no-store",
    });

    if (!response.ok) {
        let errorMessage = "Une erreur est survenue";
        let errorCode: string | undefined;

        try {
            const errorData = await response.json();
            errorMessage = errorData.message || errorData.error || errorMessage;
            errorCode = errorData.code;
        } catch {
            // If response is not JSON, use status text
            errorMessage = response.statusText || errorMessage;
        }

        throw new ApiClientError(errorMessage, response.status, errorCode);
    }

    // Handle empty responses (204 No Content)
    if (response.status === 204) {
        return undefined as T;
    }

    // Handle responses with no content
    const contentLength = response.headers.get("content-length");
    if (contentLength === "0") {
        return undefined as T;
    }

    return response.json();
}

/**
 * GET request helper
 */
export async function apiGet<T>(endpoint: string, options?: RequestInit): Promise<T> {
    return apiClient<T>(endpoint, { ...options, method: "GET" });
}

/**
 * POST request helper
 */
export async function apiPost<T>(
    endpoint: string,
    body?: unknown,
    options?: RequestInit
): Promise<T> {
    return apiClient<T>(endpoint, {
        ...options,
        method: "POST",
        body: body ? JSON.stringify(body) : undefined,
    });
}

/**
 * PUT request helper
 */
export async function apiPut<T>(
    endpoint: string,
    body?: unknown,
    options?: RequestInit
): Promise<T> {
    return apiClient<T>(endpoint, {
        ...options,
        method: "PUT",
        body: body ? JSON.stringify(body) : undefined,
    });
}

/**
 * DELETE request helper
 */
export async function apiDelete<T>(endpoint: string, options?: RequestInit): Promise<T> {
    return apiClient<T>(endpoint, { ...options, method: "DELETE" });
}
