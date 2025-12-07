/*
  Axios Instance - The Bridge to Spring Boot
  -------------------------------------------
  This file creates a configured axios instance that:
  1. Sets the base URL to your backend
  2. Automatically injects JWT token on every request
  3. Handles 401 errors (expired token → redirect to login)
  
  ALL API calls in the app should use this instance, not raw axios.
*/

import axios from 'axios';

// Create axios instance with default config
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8088/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 second timeout
});

/*
  REQUEST INTERCEPTOR
  -------------------
  Runs BEFORE every request is sent.
  If a JWT token exists in localStorage, attach it to the Authorization header.
*/
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/*
  RESPONSE INTERCEPTOR
  --------------------
  Runs AFTER every response is received.
  If we get a 401 (Unauthorized), the token is expired or invalid.
  We clear it and redirect to login.
*/
api.interceptors.response.use(
  (response) => {
    // Success - just return the response
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('token');
      localStorage.removeItem('user');

      // Only redirect if not already on login/register page
      if (
        !window.location.pathname.includes('/login') &&
        !window.location.pathname.includes('/register')
      ) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
