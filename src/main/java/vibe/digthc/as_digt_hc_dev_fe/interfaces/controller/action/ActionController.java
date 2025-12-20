package vibe.digthc.as_digt_hc_dev_fe.interfaces.controller.action;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vibe.digthc.as_digt_hc_dev_fe.domain.action.dto.ActionCardResponse;
import vibe.digthc.as_digt_hc_dev_fe.domain.action.dto.ActionStatsResponse;
import vibe.digthc.as_digt_hc_dev_fe.domain.action.service.ActionCardService;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.CurrentUserId;
import vibe.digthc.as_digt_hc_dev_fe.interfaces.common.ApiResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
public class ActionController {

    private final ActionCardService actionCardService;

    @GetMapping("/today")
    public ApiResponse<List<ActionCardResponse>> getTodayActions(@CurrentUserId UUID userId) {
        return ApiResponse.success(actionCardService.getTodayActions(userId));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<ActionCardResponse> completeAction(
            @CurrentUserId UUID userId,
            @PathVariable Long id) {
        return ApiResponse.success(actionCardService.completeAction(id, userId));
    }

    /**
     * 행동 카드 스킵
     */
    @PostMapping("/{id}/skip")
    public ApiResponse<ActionCardResponse> skipAction(
            @CurrentUserId UUID userId,
            @PathVariable Long id) {
        return ApiResponse.success(actionCardService.skipAction(id, userId));
    }

    @GetMapping("/stats")
    public ApiResponse<ActionStatsResponse> getStatistics(@CurrentUserId UUID userId) {
        return ApiResponse.success(actionCardService.getStatistics(userId));
    }
}

