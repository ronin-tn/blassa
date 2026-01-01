package com.blassa.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ReportDTO {
    private Long id;
    private String reason;
    private String description;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime resolvedAt;
    private ReporterInfo reporter;
    private ReportedUserInfo reportedUser;
    private RideInfo ride;

    @Data
    @Builder
    public static class ReporterInfo {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
    }

    @Data
    @Builder
    public static class ReportedUserInfo {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
    }

    @Data
    @Builder
    public static class RideInfo {
        private UUID id;
        private String originName;
        private String destinationName;
    }
}
