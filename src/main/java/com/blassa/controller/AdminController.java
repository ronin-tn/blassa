package com.blassa.controller;

import com.blassa.model.enums.ReportStatus;
import com.blassa.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<com.blassa.dto.UserSummaryDTO>> getUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<Void> banUser(@PathVariable UUID id) {
        adminService.banUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable UUID id) {
        adminService.unbanUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reports")
    public ResponseEntity<List<com.blassa.dto.ReportDTO>> getReports() {
        return ResponseEntity.ok(adminService.getReports());
    }

    @PutMapping("/reports/{id}/resolve")
    public ResponseEntity<Void> resolveReport(@PathVariable Long id, @RequestParam ReportStatus status) {
        adminService.resolveReport(id, status);
        return ResponseEntity.ok().build();
    }
}
