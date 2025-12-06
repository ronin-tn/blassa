package com.blassa.controller;

import com.blassa.dto.RideRequest;
import com.blassa.dto.RideResponse;
import com.blassa.dto.RideStatusResponse;
import com.blassa.dto.RideUpdateStatusRequest;
import com.blassa.repository.RideRepository;
import com.blassa.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideRepository rideRepository;
    private final RideService rideService;

    @PostMapping
    public ResponseEntity<RideResponse> createRide(@RequestBody @Valid RideRequest request) {
        return ResponseEntity.ok(rideService.createRide(request));
    }

    // SEARCH FOR RIDE (PUBLIC - supports lazy registration)
    @GetMapping("/search")
    public ResponseEntity<Page<RideResponse>> searchRides(
            @RequestParam Double originLat,
            @RequestParam Double originLon,
            @RequestParam Double destLat,
            @RequestParam Double destLon,
            // Use String and parse manually to avoid Spring conversion issues
            @RequestParam(required = false) String departureTime,
            @RequestParam(defaultValue = "1") Integer seats,

            // Optional: For anonymous users to filter by gender preference
            @RequestParam(required = false) String genderFilter,

            @RequestParam(defaultValue = "3.0") Double radius, // in Km
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Manual parsing is more robust against URL encoding/format issues
        OffsetDateTime offsetTime = null;
        if (departureTime != null && !departureTime.isBlank()) {
            java.time.LocalDateTime localTime = java.time.LocalDateTime.parse(departureTime);
            // Convert LocalDateTime to OffsetDateTime with Tunisia timezone (+01:00)
            offsetTime = localTime.atOffset(java.time.ZoneOffset.of("+01:00"));
        }

        return ResponseEntity.ok(rideService.searchRides(
                originLat, originLon,
                destLat, destLon,
                offsetTime,
                seats,
                genderFilter,
                radius,
                page,
                size));
    }

    // VIEW RIDE BY ID
    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(rideService.getRideById(id));
    }

    // VIEW MY RIDES
    @GetMapping("/mine")
    public ResponseEntity<Page<RideResponse>> getMyRides(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(rideService.getMyRides(page, size));
    }

    // DELETE RIDE
    @DeleteMapping("/{id}")
    public ResponseEntity<RideResponse> cancelRide(@PathVariable UUID id) {
        return ResponseEntity.ok(rideService.cancelRide(id));
    }

    // UPDATE RIDE
    @PutMapping("/{id}")
    public ResponseEntity<RideResponse> updateRide(
            @PathVariable UUID id,
            @RequestBody @Valid RideRequest request) {
        return ResponseEntity.ok(rideService.updateRide(id, request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RideStatusResponse> updateStatus(@PathVariable UUID id,
            @RequestBody @Valid RideUpdateStatusRequest request) {
        return ResponseEntity.ok(rideService.updateRideStatus(id, request));
    }

    // Phase 7: Dedicated lifecycle endpoints
    @PutMapping("/{id}/start")
    public ResponseEntity<RideStatusResponse> startRide(@PathVariable UUID id) {
        return ResponseEntity.ok(rideService.startRide(id));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<RideStatusResponse> completeRide(@PathVariable UUID id) {
        return ResponseEntity.ok(rideService.completeRide(id));
    }
}
