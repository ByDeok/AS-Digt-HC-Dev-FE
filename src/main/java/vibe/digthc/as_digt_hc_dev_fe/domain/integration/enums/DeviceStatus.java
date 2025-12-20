package vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums;

/**
 * 디바이스 연동 상태
 */
public enum DeviceStatus {
    PENDING,  // 연결 대기
    ACTIVE,   // 활성 (정상 연동)
    EXPIRED,  // 토큰 만료
    REVOKED,  // 연동 해제됨
    ERROR     // 오류
}

