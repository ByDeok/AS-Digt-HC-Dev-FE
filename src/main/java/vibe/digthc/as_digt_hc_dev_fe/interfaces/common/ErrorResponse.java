package vibe.digthc.as_digt_hc_dev_fe.interfaces.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 에러 응답 포맷
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * 에러 코드
     */
    private String code;
    
    /**
     * 에러 메시지
     */
    private String message;
    
    /**
     * 상세 에러 메시지 (개발 환경에서만 노출)
     */
    private String detail;
    
    /**
     * 필드 검증 에러 목록
     */
    private List<FieldError> fieldErrors;
    
    /**
     * 에러 발생 시간
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * 필드 검증 에러 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String rejectedValue;
        private String message;
    }
}

