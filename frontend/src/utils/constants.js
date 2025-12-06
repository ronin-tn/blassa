/*
  🎓 LEARNING: Constants & Enums in JavaScript
  ---------------------------------------------
  Unlike Java, JavaScript doesn't have enums.
  We use plain objects with UPPERCASE keys instead.
  
  These mirror your Spring Boot enums EXACTLY:
  - Gender.java → Gender
  - RideStatus.java → RideStatus
  - BookingStatus.java → BookingStatus
  - RideGenderPreference.java → RideGenderPreference
  
  Why? When sending data to your backend, we need the exact
  same string values ("MALE", "FEMALE", etc.)
*/

// Matches: com.blassa.model.enums.Gender
export const Gender = {
    MALE: 'MALE',
    FEMALE: 'FEMALE'
};

// Matches: com.blassa.model.enums.RideGenderPreference
export const RideGenderPreference = {
    ANY: 'ANY',
    FEMALE_ONLY: 'FEMALE_ONLY',
    MALE_ONLY: 'MALE_ONLY'
};

// Matches: com.blassa.model.enums.RideStatus
export const RideStatus = {
    SCHEDULED: 'SCHEDULED',
    FULL: 'FULL',
    IN_PROGRESS: 'IN_PROGRESS',
    COMPLETED: 'COMPLETED',
    CANCELLED: 'CANCELLED'
};

// Matches: com.blassa.model.enums.BookingStatus
export const BookingStatus = {
    PENDING: 'PENDING',
    CONFIRMED: 'CONFIRMED',
    REJECTED: 'REJECTED',
    CANCELLED: 'CANCELLED'
};

// Helper: Get display label for enum values
// Usage: getGenderLabel('FEMALE') → 'Female'
export const getGenderLabel = (value) => {
    const labels = {
        MALE: 'Male',
        FEMALE: 'Female'
    };
    return labels[value] || value;
};

export const getRideStatusLabel = (value) => {
    const labels = {
        SCHEDULED: 'Scheduled',
        FULL: 'Full',
        IN_PROGRESS: 'In Progress',
        COMPLETED: 'Completed',
        CANCELLED: 'Cancelled'
    };
    return labels[value] || value;
};

export const getBookingStatusLabel = (value) => {
    const labels = {
        PENDING: 'Pending',
        CONFIRMED: 'Confirmed',
        REJECTED: 'Rejected',
        CANCELLED: 'Cancelled'
    };
    return labels[value] || value;
};
