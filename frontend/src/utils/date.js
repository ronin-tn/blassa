/*
  🎓 LEARNING: Date Utilities
  ---------------------------
  Spring Boot uses ISO 8601 date format: "2024-12-06T14:30:00+01:00"
  JavaScript Date objects need to be formatted for sending to backend.
  
  These utilities handle the conversion between JS Date and ISO strings.
*/

/**
 * Format a Date object to ISO string for backend
 * @param {Date} date - JavaScript Date object
 * @returns {string} ISO 8601 formatted string
 */
export const toISOString = (date) => {
    if (!date) return null;
    return date.toISOString();
};

/**
 * Parse ISO date string from backend to Date object
 * @param {string} isoString - ISO 8601 string from backend
 * @returns {Date} JavaScript Date object
 */
export const parseDate = (isoString) => {
    if (!isoString) return null;
    return new Date(isoString);
};

/**
 * Format date for display (human readable)
 * @param {string|Date} date - Date to format
 * @returns {string} Formatted like "Dec 6, 2024 at 2:30 PM"
 */
export const formatDateTime = (date) => {
    if (!date) return '';
    const d = typeof date === 'string' ? new Date(date) : date;

    return new Intl.DateTimeFormat('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
        hour: 'numeric',
        minute: '2-digit',
        hour12: true
    }).format(d);
};

/**
 * Format date only (no time)
 * @param {string|Date} date - Date to format
 * @returns {string} Formatted like "Dec 6, 2024"
 */
export const formatDate = (date) => {
    if (!date) return '';
    const d = typeof date === 'string' ? new Date(date) : date;

    return new Intl.DateTimeFormat('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
    }).format(d);
};

/**
 * Format time only
 * @param {string|Date} date - Date to format
 * @returns {string} Formatted like "2:30 PM"
 */
export const formatTime = (date) => {
    if (!date) return '';
    const d = typeof date === 'string' ? new Date(date) : date;

    return new Intl.DateTimeFormat('en-US', {
        hour: 'numeric',
        minute: '2-digit',
        hour12: true
    }).format(d);
};

/**
 * Get relative time (e.g., "in 2 hours", "3 days ago")
 * @param {string|Date} date - Date to compare
 * @returns {string} Relative time string
 */
export const getRelativeTime = (date) => {
    if (!date) return '';
    const d = typeof date === 'string' ? new Date(date) : date;
    const now = new Date();
    const diffMs = d - now;
    const diffMins = Math.round(diffMs / 60000);
    const diffHours = Math.round(diffMs / 3600000);
    const diffDays = Math.round(diffMs / 86400000);

    if (Math.abs(diffMins) < 60) {
        return diffMins >= 0 ? `in ${diffMins} min` : `${Math.abs(diffMins)} min ago`;
    }
    if (Math.abs(diffHours) < 24) {
        return diffHours >= 0 ? `in ${diffHours}h` : `${Math.abs(diffHours)}h ago`;
    }
    return diffDays >= 0 ? `in ${diffDays} days` : `${Math.abs(diffDays)} days ago`;
};
