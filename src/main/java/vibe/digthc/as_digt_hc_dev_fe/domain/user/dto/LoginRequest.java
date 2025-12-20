package vibe.digthc.as_digt_hc_dev_fe.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
    /**
     * 로그인 식별자
     * - 기본: 이메일
     * - 로컬/테스트 편의: 고정 관리자 계정 "admin" 허용
     *
     * ⚠️ 운영 환경에서는 이메일 로그인만 허용하는 것이 일반적이므로,
     *    'admin' 허용은 테스트 편의를 위한 최소 범위 예외입니다.
     */
    @NotBlank
    @Pattern(
        regexp = "^(admin|[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,})$",
        message = "이메일 형식이 올바르지 않습니다. (또는 admin)"
    )
    String email,
    @NotBlank String password
) {}

