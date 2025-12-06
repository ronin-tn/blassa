/*
  Auth API - Mirrors AuthenticationController.java
  -------------------------------------------------
  This file contains all API calls related to authentication.
  
  Maps to: com.blassa.controller.AuthenticationController
  
  Endpoints:
  - POST /auth/register → register()
  - POST /auth/login → login()
*/

import api from './axios';

/**
 * Register a new user
 * 
 * Maps to: AuthenticationController.register()
 * DTO: RegisterRequest
 * 
 * @param {Object} data - Registration data
 * @param {string} data.email - User email
 * @param {string} data.password - User password
 * @param {string} data.firstName - First name
 * @param {string} data.lastName - Last name
 * @param {string} data.phoneNumber - Phone in E.164 format (e.g., +21612345678)
 * @param {string} data.gender - "MALE" or "FEMALE"
 * @param {string} data.birthDate - Date in YYYY-MM-DD format
 * @returns {Promise<{token: string}>} - JWT token
 */
export const register = async (data) => {
    const response = await api.post('/auth/register', data);
    return response.data;
};

/**
 * Login with email and password
 * 
 * Maps to: AuthenticationController.authenticate()
 * DTO: AuthenticationRequest
 * 
 * @param {Object} data - Login credentials
 * @param {string} data.email - User email
 * @param {string} data.password - User password
 * @returns {Promise<{token: string}>} - JWT token
 */
export const login = async (data) => {
    const response = await api.post('/auth/login', data);
    return response.data;
};

// Export all auth functions
export default {
    register,
    login,
};
