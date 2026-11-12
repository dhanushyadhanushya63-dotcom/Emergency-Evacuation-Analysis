package com.evacuation.controller;

import com.evacuation.dto.ReportDto;
import com.evacuation.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/{scenarioId}")
    public ResponseEntity<ReportDto> getReport(@PathVariable Long scenarioId) {
        return ResponseEntity.ok(reportService.generateReport(scenarioId));
    }
}
