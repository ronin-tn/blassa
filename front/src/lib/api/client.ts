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

    if (token) {
        (headers as Record<string, string>)["Authorization"] = `Bearer ${token}`;
    }

    const url = endpoint.startsWith("http") ? endpoint : `${API_URL}${endpoint}`;

    const response = await fetch(url, {
        ...options,
        headers,
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

    if (response.status === 204) {
        return undefined as T;
    }
    const contentLength = response.headers.get("content-length");
    if (contentLength === "0") {
        return undefined as T;
    }

    return response.json();
}

export async function apiGet<T>(endpoint: string, options?: RequestInit): Promise<T> {
    return apiClient<T>(endpoint, { ...options, method: "GET" });
}

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

export async function apiDelete<T>(endpoint: string, options?: RequestInit): Promise<T> {
    return apiClient<T>(endpoint, { ...options, method: "DELETE" });
}
