/*
  Ride API Functions
  ------------------
  API calls for ride-related operations.
  
  Public endpoints (no auth required):
  - searchRides: Search for available rides
  
  Protected endpoints (auth required):
  - createRide, getMyRides, cancelRide, updateRide
*/

import api from './axios';

/**
 * Search for rides (PUBLIC - no auth required)
 * Supports lazy registration: users can browse without logging in
 * 
 * @param {Object} params Search parameters
 * @param {number} params.originLat Origin latitude
 * @param {number} params.originLon Origin longitude
 * @param {number} params.destLat Destination latitude
 * @param {number} params.destLon Destination longitude
 * @param {string} params.departureTime ISO datetime string
 * @param {number} params.seats Number of seats needed
 * @param {string} params.genderFilter Optional: 'MALE' or 'FEMALE' for anonymous users
 * @param {number} params.radius Search radius in km (default: 3)
 * @param {number} params.page Page number (default: 0)
 * @param {number} params.size Page size (default: 10)
 */
export const searchRides = async (params) => {
    const response = await api.get('/rides/search', { params });
    return response.data;
};

/**
 * Get ride by ID (PUBLIC - no auth required)
 */
export const getRideById = async (id) => {
    const response = await api.get(`/rides/${id}`);
    return response.data;
};

/**
 * Create a new ride (PROTECTED)
 */
export const createRide = async (rideData) => {
    const response = await api.post('/rides', rideData);
    return response.data;
};

/**
 * Get current user's rides (PROTECTED)
 */
export const getMyRides = async (page = 0, size = 10) => {
    const response = await api.get('/rides/mine', { params: { page, size } });
    return response.data;
};

/**
 * Cancel a ride (PROTECTED)
 */
export const cancelRide = async (id) => {
    const response = await api.delete(`/rides/${id}`);
    return response.data;
};

/**
 * Update a ride (PROTECTED)
 */
export const updateRide = async (id, rideData) => {
    const response = await api.put(`/rides/${id}`, rideData);
    return response.data;
};

export default {
    searchRides,
    getRideById,
    createRide,
    getMyRides,
    cancelRide,
    updateRide,
};
