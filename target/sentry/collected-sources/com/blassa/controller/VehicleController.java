package com.blassa.controller;

import com.blassa.dto.VehicleDTO;
import com.blassa.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody VehicleDTO request) {
        return ResponseEntity.ok(vehicleService.createVehicle(request));
    }

    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getMyVehicles() {
        return ResponseEntity.ok(vehicleService.getMyVehicles());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable UUID id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
