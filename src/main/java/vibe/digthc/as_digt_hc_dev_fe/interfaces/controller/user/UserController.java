package vibe.digthc.as_digt_hc_dev_fe.interfaces.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.dto.ProfileResponse;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.dto.ProfileUpdateRequest;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.service.AuthService;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.CurrentUserId;
import vibe.digthc.as_digt_hc_dev_fe.interfaces.common.ApiResponse;

import java.util.UUID;

/**
 * 사용자 프로필 관리 Controller
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    /**
     * 내 프로필 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(@CurrentUserId UUID userId) {
        ProfileResponse profile = authService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공", profile));
    }

    /**
     * 프로필 수정
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @CurrentUserId UUID userId,
            @Valid @RequestBody ProfileUpdateRequest request) {
        ProfileResponse profile = authService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("프로필 수정 성공", profile));
    }
}
