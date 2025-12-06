Phase 6: Reviews & Ratings System
You already have the
Review
entity. This phase would implement:

Passengers rate drivers after a completed ride
Drivers rate passengers
Calculate average ratings (displayed in RideResponse.driverRating)
Prevent duplicate reviews
Phase 7: Ride Lifecycle Management
startRide() → Change status to IN_PROGRESS
completeRide() → Change status to COMPLETED
Auto-trigger review eligibility after completion
Phase 8: Notifications (Optional)
SMS/Push notifications for booking confirmations
Ride reminders before departure
Phase 9: Driver Dashboard
View all bookings for a driver's rides
Accept/Reject bookings (if you want PENDING → CONFIRMED flow)
