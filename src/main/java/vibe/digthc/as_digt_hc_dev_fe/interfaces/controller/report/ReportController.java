package vibe.digthc.as_digt_hc_dev_fe.interfaces.controller.report;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.dto.HealthReportResponse;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.entity.HealthReport;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.entity.PeriodType;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.service.HealthReportService;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.CurrentUserId;
import vibe.digthc.as_digt_hc_dev_fe.interfaces.common.ApiResponse;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final HealthReportService healthReportService;

    @PostMapping("/generate")
    public ApiResponse<HealthReportResponse> generateReport(
            @CurrentUserId UUID userId,
            @RequestParam(value = "periodType", required = false) PeriodType periodType) {
        HealthReport report = healthReportService.generateReport(userId, periodType);
        return ApiResponse.success("Health report generated successfully", HealthReportResponse.from(report));
    }


    @GetMapping("/{id}")
    public ApiResponse<HealthReportResponse> getReport(@PathVariable UUID id) {
        HealthReport report = healthReportService.getReport(id);
        return ApiResponse.success("Health report retrieved successfully", HealthReportResponse.from(report));
    }

    @GetMapping
    public ApiResponse<List<HealthReportResponse>> getReports(
            @CurrentUserId UUID userId,
            @RequestParam(value = "periodType", required = false) PeriodType periodType) {
        List<HealthReportResponse> reports = healthReportService.getReportsByUser(userId, periodType).stream()
                .map(HealthReportResponse::from)
                .collect(Collectors.toList());
        return ApiResponse.success("Health reports retrieved successfully", reports);
    }


    /**
     * 리포트 삭제
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteReport(
            @CurrentUserId UUID userId,
            @PathVariable UUID id) {
        healthReportService.deleteReport(userId, id);
        return ApiResponse.success("Health report deleted successfully");
    }
}

