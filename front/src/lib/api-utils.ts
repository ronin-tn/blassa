
export async function parseApiError(response: Response, fallbackMessage: string = "Une erreur est survenue"): Promise<string> {
    try {
        const text = await response.text();


        try {
            const json = JSON.parse(text);

            if (json.message) {
                return json.message;
            }

            if (json.error && typeof json.error === 'string') {
                return json.error;
            }
            if (json.errors && Array.isArray(json.errors)) {
                return json.errors.map((e: { message?: string }) => e.message || e).join(', ');
            }
        } catch {

            if (text && text.length < 200) {
                return text;
            }
        }

        return fallbackMessage;
    } catch {
        return fallbackMessage;
    }
}


export async function apiRequest<T>(
    url: string,
    options: RequestInit = {}
): Promise<T> {
    const response = await fetch(url, options);

    if (!response.ok) {
        const errorMessage = await parseApiError(response);
        throw new Error(errorMessage);
    }


    const text = await response.text();
    if (!text) {
        return {} as T;
    }

    return JSON.parse(text);
}
