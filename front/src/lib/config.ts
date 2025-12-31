


export const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8088/api/v1";
export const WS_URL = process.env.NEXT_PUBLIC_WS_URL || "http://localhost:8088/ws";


export const BASE_URL = API_URL.replace("/api/v1", "");


export const AUTH_COOKIE_NAME = "blassa_token";


export const GOOGLE_OAUTH_URL = `${BASE_URL}/oauth2/authorization/google`;
