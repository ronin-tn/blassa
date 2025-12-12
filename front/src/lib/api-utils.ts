/**
 * Extracts a user-friendly error message from an API response
 * Handles both JSON error responses and plain text responses
 */
export async function parseApiError(response: Response, fallbackMessage: string = "Une erreur est survenue"): Promise<string> {
    try {
        const text = await response.text();

        // Try to parse as JSON
        try {
            const json = JSON.parse(text);
            // Handle our backend error format: { error, message, timestamp, status }
            if (json.message) {
                return json.message;
            }
            // Handle other common error formats
            if (json.error && typeof json.error === 'string') {
                return json.error;
            }
            if (json.errors && Array.isArray(json.errors)) {
                return json.errors.map((e: { message?: string }) => e.message || e).join(', ');
            }
        } catch {
            // Not JSON, use the text as-is if it's not empty
            if (text && text.length < 200) {
                return text;
            }
        }

        return fallbackMessage;
    } catch {
        return fallbackMessage;
    }
}

/**
 * Makes an API request and throws a user-friendly error if it fails
 */
export async function apiRequest<T>(
    url: string,
    options: RequestInit = {}
): Promise<T> {
    const response = await fetch(url, options);

    if (!response.ok) {
        const errorMessage = await parseApiError(response);
        throw new Error(errorMessage);
    }

    // Handle empty responses (like 204 No Content)
    const text = await response.text();
    if (!text) {
        return {} as T;
    }

    return JSON.parse(text);
}
