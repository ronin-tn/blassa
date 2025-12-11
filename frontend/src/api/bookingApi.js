/*
  Booking API Functions
  ---------------------
  API calls for booking-related operations.
  
  Protected endpoints (auth required):
  - getMyBookedRideIds: Get ride IDs user has already booked
  - getMyBookings: Get user's booking history
  - createBooking: Book a seat on a ride
  - cancelBooking: Cancel a booking
*/

import api from './axios';

/**
 * Get all ride IDs that the current user has booked (for preventing duplicate reservations).
 * Only works for authenticated users.
 * @returns {Promise<string[]>} Array of ride UUID strings
 */
export const getMyBookedRideIds = async () => {
    const response = await api.get('/bookings/mine/ride-ids');
    return response.data;
};

/**
 * Get current user's bookings (PROTECTED)
 */
export const getMyBookings = async (page = 0, size = 10) => {
    const response = await api.get('/bookings/mine', { params: { page, size } });
    return response.data;
};

/**
 * Create a new booking (PROTECTED)
 */
export const createBooking = async (rideId, seatsRequested = 1) => {
    const response = await api.post('/bookings', { rideId, seatsRequested });
    return response.data;
};

/**
 * Cancel a booking (PROTECTED)
 */
export const cancelBooking = async (bookingId) => {
    const response = await api.delete(`/bookings/${bookingId}`);
    return response.data;
};

/**
 * Get passengers for a specific ride (PROTECTED - driver only)
 * @param {string} rideId - UUID of the ride
 * @returns {Promise<Array>} Array of passenger info objects
 */
export const getPassengersForRide = async (rideId) => {
    const response = await api.get(`/bookings/ride/${rideId}/passengers`);
    return response.data;
};

/**
 * Cancel booking by ride ID (finds the user's booking for this ride and cancels it)
 * @param {string} rideId - UUID of the ride
 */
export const cancelBookingByRide = async (rideId) => {
    const response = await api.delete(`/bookings/ride/${rideId}`);
    return response.data;
};
