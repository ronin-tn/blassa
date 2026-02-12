// In production, use relative paths (Vercel rewrites proxy to fly.io)
// In local dev, set NEXT_PUBLIC_API_URL=http://localhost:8088/api/v1
export const API_URL = process.env.NEXT_PUBLIC_API_URL || "/api/v1";
export const WS_URL = process.env.NEXT_PUBLIC_WS_URL || "/ws";


export const BASE_URL = API_URL.replace("/api/v1", "");


export const AUTH_COOKIE_NAME = "blassa_token";


export const GOOGLE_OAUTH_URL = `${BASE_URL}/oauth2/authorization/google`;
