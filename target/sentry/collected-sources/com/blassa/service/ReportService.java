package com.blassa.service;

import com.blassa.dto.ReportRequest;
import com.blassa.model.entity.User;
import com.blassa.model.entity.UserReport;
import com.blassa.model.enums.ReportStatus;
import com.blassa.repository.RideRepository;
import com.blassa.repository.UserReportRepository;
import com.blassa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final UserReportRepository userReportRepository;

    @Transactional
    public void createReport(ReportRequest request) {
        User reporter = getCurrentUser();

        if (request.getReportedUserId() == null && request.getRideId() == null) {
            throw new IllegalArgumentException("Must report either a user or a ride");
        }

        UserReport.UserReportBuilder reportBuilder = UserReport.builder()
                .reporter(reporter)
                .reason(request.getReason())
                .description(request.getDescription())
                .status(ReportStatus.PENDING);

        if (request.getReportedUserId() != null) {
            User reportedUser = userRepository.findById(request.getReportedUserId())
                    .orElseThrow(() -> new RuntimeException("Reported user not found"));
            reportBuilder.reportedUser(reportedUser);
        }

        if (request.getRideId() != null) {
            var ride = rideRepository.findById(request.getRideId())
                    .orElseThrow(() -> new RuntimeException("Ride not found"));
            reportBuilder.ride(ride);
        }

        userReportRepository.save(reportBuilder.build());
    }

    private User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
