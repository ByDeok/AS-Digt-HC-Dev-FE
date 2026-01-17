package vibe.digthc.as_digt_hc_dev_fe.interfaces.controller.report;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.dto.HealthMetricDailyRequest;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.dto.HealthMetricDailyResponse;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.service.HealthMetricDailyService;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.CurrentUserId;
import vibe.digthc.as_digt_hc_dev_fe.interfaces.common.ApiResponse;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
public class HealthMetricController {

    private final HealthMetricDailyService healthMetricDailyService;

    @PostMapping("/daily")
    public ApiResponse<HealthMetricDailyResponse> upsertDailyMetrics(
            @CurrentUserId UUID userId,
            @Valid @RequestBody HealthMetricDailyRequest request) {
        return ApiResponse.success(
                "Daily health metrics saved successfully",
                healthMetricDailyService.upsertDailyMetrics(userId, request)
        );
    }

    @GetMapping("/daily")
    public ApiResponse<HealthMetricDailyResponse> getDailyMetrics(
            @CurrentUserId UUID userId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(
                "Daily health metrics retrieved successfully",
                healthMetricDailyService.getDailyMetrics(userId, date)
        );
    }
}
