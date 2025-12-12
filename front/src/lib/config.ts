// API configuration with proper production fallback
const getApiUrl = (): string => {
    // First check environment variable
    if (process.env.NEXT_PUBLIC_API_URL) {
        return process.env.NEXT_PUBLIC_API_URL;
    }

    // In production (on Render), use the production URL
    if (process.env.NODE_ENV === 'production') {
        return 'https://blassa-backend.onrender.com/api/v1';
    }

    // Development fallback
    return 'http://localhost:8088/api/v1';
};

export const API_URL = getApiUrl();

// WebSocket URL
export const WS_URL = process.env.NEXT_PUBLIC_WS_URL ||
    (process.env.NODE_ENV === 'production'
        ? 'https://blassa-backend.onrender.com/ws'
        : 'http://localhost:8088/ws');
