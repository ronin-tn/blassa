package com.blassa.controller;

import com.blassa.dto.ReportRequest;
import com.blassa.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Void> createReport(@RequestBody @Valid ReportRequest request) {
        reportService.createReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
