package com.blassa.controller;

import com.blassa.dto.BookingRequest;
import com.blassa.dto.BookingResponse;
import com.blassa.dto.RidePassengerResponse;
import com.blassa.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody @Valid BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping("/mine")
    public ResponseEntity<Page<BookingResponse>> getMyBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.getMyBookings(page, size));
    }

    /**
     * Get all ride IDs that the current user has booked (for disabling duplicate
     * reservations).
     */
    @GetMapping("/mine/ride-ids")
    public ResponseEntity<java.util.List<java.util.UUID>> getMyBookedRideIds() {
        return ResponseEntity.ok(bookingService.getMyBookedRideIds());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Get all passengers for a specific ride (for drivers only).
     */
    @GetMapping("/ride/{rideId}/passengers")
    public ResponseEntity<java.util.List<RidePassengerResponse>> getPassengersForRide(
            @PathVariable UUID rideId) {
        return ResponseEntity.ok(bookingService.getPassengersForRide(rideId));
    }

    /**
     * Get current user's booking for a specific ride (for review page).
     */
    @GetMapping("/ride/{rideId}/mine")
    public ResponseEntity<BookingResponse> getMyBookingForRide(@PathVariable UUID rideId) {
        return ResponseEntity.ok(bookingService.getMyBookingForRide(rideId));
    }

    /**
     * Cancel current user's booking for a specific ride.
     */
    @DeleteMapping("/ride/{rideId}")
    public ResponseEntity<Void> cancelBookingByRide(@PathVariable UUID rideId) {
        bookingService.cancelBookingByRide(rideId);
        return ResponseEntity.ok().build();
    }

    /**
     * Driver accepts a pending booking.
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<BookingResponse> acceptBooking(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.acceptBooking(id));
    }

    /**
     * Driver rejects a pending booking.
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectBooking(@PathVariable UUID id) {
        bookingService.rejectBooking(id);
        return ResponseEntity.ok().build();
    }
}
