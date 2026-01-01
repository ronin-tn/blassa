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

    // Recherche t3 trajet
    @GetMapping("/search")
    public ResponseEntity<Page<RideResponse>> searchRides(
            @RequestParam Double originLat,
            @RequestParam Double originLon,
            @RequestParam Double destLat,
            @RequestParam Double destLon,
            @RequestParam(required = false) String departureTime,
            @RequestParam(defaultValue = "1") Integer seats,

            @RequestParam(required = false) String genderFilter,

            @RequestParam(defaultValue = "3.0") Double radius,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price_asc") String sortBy) {

        OffsetDateTime offsetTime = null;
        if (departureTime != null && !departureTime.isBlank()) {
            java.time.LocalDateTime localTime = java.time.LocalDateTime.parse(departureTime);
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
                size,
                sortBy));
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

    @PutMapping("/{id}/start")
    public ResponseEntity<RideStatusResponse> startRide(@PathVariable UUID id) {
        return ResponseEntity.ok(rideService.startRide(id));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<RideStatusResponse> completeRide(@PathVariable UUID id) {
        return ResponseEntity.ok(rideService.completeRide(id));
    }
}
