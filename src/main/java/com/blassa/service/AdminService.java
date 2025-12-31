package com.blassa.service;

import com.blassa.model.entity.User;
import com.blassa.model.entity.UserReport;
import com.blassa.model.enums.ReportStatus;
import com.blassa.repository.RideRepository;
import com.blassa.repository.UserReportRepository;
import com.blassa.dto.ReportDTO;
import com.blassa.model.entity.UserReport;
import com.blassa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final UserReportRepository userReportRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalRides", rideRepository.count());
        stats.put("pendingReports", userReportRepository.findByStatusOrderByCreatedAtDesc(ReportStatus.PENDING).size());
        return stats;
    }

    public List<com.blassa.dto.UserSummaryDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> com.blassa.dto.UserSummaryDTO.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .role(user.getRole())
                        .isVerified(user.isVerified())
                        .emailVerified(user.getEmailVerified())
                        .deletedAt(user.getDeletedAt() != null ? user.getDeletedAt().toString() : null)
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void banUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeletedAt(OffsetDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void unbanUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeletedAt(null);
        userRepository.save(user);
    }

    public List<ReportDTO> getReports() {
        return userReportRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToReportDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    private ReportDTO mapToReportDTO(UserReport report) {
        ReportDTO.ReportDTOBuilder builder = ReportDTO.builder()
                .id(report.getId())
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus().name())
                .createdAt(report.getCreatedAt())
                .resolvedAt(report.getResolvedAt());

        if (report.getReporter() != null) {
            builder.reporter(ReportDTO.ReporterInfo.builder()
                    .id(report.getReporter().getId())
                    .firstName(report.getReporter().getFirstName())
                    .lastName(report.getReporter().getLastName())
                    .email(report.getReporter().getEmail())
                    .phone(report.getReporter().getPhoneNumber())
                    .build());
        }

        if (report.getReportedUser() != null) {
            builder.reportedUser(ReportDTO.ReportedUserInfo.builder()
                    .id(report.getReportedUser().getId())
                    .firstName(report.getReportedUser().getFirstName())
                    .lastName(report.getReportedUser().getLastName())
                    .email(report.getReportedUser().getEmail())
                    .phone(report.getReportedUser().getPhoneNumber())
                    .build());
        }

        if (report.getRide() != null) {
            builder.ride(ReportDTO.RideInfo.builder()
                    .id(report.getRide().getId())
                    .originName(report.getRide().getOriginName())
                    .destinationName(report.getRide().getDestinationName())
                    .build());
        }

        return builder.build();
    }

    @Transactional
    public void resolveReport(Long reportId, ReportStatus status) {
        UserReport report = userReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setStatus(status);
        report.setResolvedAt(OffsetDateTime.now());
        userReportRepository.save(report);
    }
}
