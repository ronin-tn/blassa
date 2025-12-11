/*
  User API - User Profile Endpoints
  ----------------------------------
  API functions for user profile operations.
*/

import api from './axios';

/**
 * Get the current user's profile
 * @returns {Promise<Object>} User profile data
 */
export const getMyProfile = async () => {
    const response = await api.get('/user/me');
    return response.data;
};

/**
 * Update the current user's profile
 * @param {Object} profileData - Profile data to update
 * @returns {Promise<Object>} Updated profile data
 */
export const updateProfile = async (profileData) => {
    const response = await api.put('/user/me', profileData);
    return response.data;
};
