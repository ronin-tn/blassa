package com.blassa.dto;

import java.util.UUID;

public record VehicleDTO(
        UUID id,
        String make,
        String model,
        String color,
        String licensePlate,
        Integer productionYear) {
}
