/**
 * Centralized configuration for the Blassa frontend
 * 
 * All environment-dependent values should be accessed through this module.
 */

// API Configuration
export const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8088/api/v1";
export const WS_URL = process.env.NEXT_PUBLIC_WS_URL || "http://localhost:8088/ws";

// Base URL (API without /api/v1 suffix)
export const BASE_URL = API_URL.replace("/api/v1", "");

// Auth Configuration
export const AUTH_COOKIE_NAME = "blassa_token";

// OAuth URLs
export const GOOGLE_OAUTH_URL = `${BASE_URL}/oauth2/authorization/google`;
