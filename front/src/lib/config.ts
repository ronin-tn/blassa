// In production, use relative paths (Vercel rewrites proxy to fly.io)
// In local dev, set NEXT_PUBLIC_API_URL=http://localhost:8088/api/v1
export const API_URL = process.env.NEXT_PUBLIC_API_URL || "/api/v1";
export const WS_URL = process.env.NEXT_PUBLIC_WS_URL || "/ws";

export const BASE_URL = API_URL.replace("/api/v1", "");

export const AUTH_COOKIE_NAME = "blassa_token";

export const GOOGLE_CLIENT_ID = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID || "";

/**
 * Build Google OAuth URL for direct frontend-to-Google redirect.
 * The callback goes to /oauth-callback on the frontend.
 */
export function getGoogleOAuthUrl(): string {
    const redirectUri = `${window.location.origin}/oauth-callback`;
    const params = new URLSearchParams({
        client_id: GOOGLE_CLIENT_ID,
        redirect_uri: redirectUri,
        response_type: "code",
        scope: "email profile",
        access_type: "offline",
        prompt: "select_account",
    });
    return `https://accounts.google.com/o/oauth2/v2/auth?${params.toString()}`;
}

