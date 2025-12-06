package com.blassa.dto;

import com.blassa.model.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RideStatusResponse {
    private UUID rideId;
    private RideStatus status;
}
