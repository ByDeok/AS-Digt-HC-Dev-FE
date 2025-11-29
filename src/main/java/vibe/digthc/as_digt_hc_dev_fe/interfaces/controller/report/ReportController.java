package vibe.digthc.as_digt_hc_dev_fe.interfaces.controller.report;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.entity.HealthReport;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.service.HealthReportService;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.CurrentUserId;
import vibe.digthc.as_digt_hc_dev_fe.interfaces.common.ApiResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final HealthReportService healthReportService;

    @PostMapping("/generate")
    public ApiResponse<HealthReport> generateReport(@CurrentUserId UUID userId) {
        HealthReport report = healthReportService.generateReport(userId);
        return ApiResponse.success("Health report generated successfully", report);
    }

    @GetMapping("/{id}")
    public ApiResponse<HealthReport> getReport(@PathVariable UUID id) {
        HealthReport report = healthReportService.getReport(id);
        return ApiResponse.success("Health report retrieved successfully", report);
    }

    @GetMapping
    public ApiResponse<List<HealthReport>> getReports(@CurrentUserId UUID userId) {
        List<HealthReport> reports = healthReportService.getReportsByUser(userId);
        return ApiResponse.success("Health reports retrieved successfully", reports);
    }
}

