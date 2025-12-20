package vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums;

/**
 * 포털 연동 상태
 */
public enum PortalStatus {
    PENDING,      // 연결 대기
    ACTIVE,       // 활성
    FAILED,       // 연결 실패
    UNSUPPORTED,  // 미지원 지역/포털
    REVOKED       // 연동 해제됨
}

