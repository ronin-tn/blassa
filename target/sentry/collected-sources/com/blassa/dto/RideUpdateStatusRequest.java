package com.blassa.dto;

import com.blassa.model.enums.RideStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class RideUpdateStatusRequest {
    @NotNull(message = "Status is required")
    private RideStatus status;
}
