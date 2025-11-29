package vibe.digthc.as_digt_hc_dev_fe.interfaces.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.exception.UnsupportedRegionException;

/**
 * 전역 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * UnsupportedRegionException 처리
     */
    @ExceptionHandler(UnsupportedRegionException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedRegionException(
            UnsupportedRegionException ex) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("UNSUPPORTED_REGION")
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Unsupported region: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * 필드 검증 예외 처리 (@Valid 실패)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .rejectedValue(error.getRejectedValue() != null 
                                ? error.getRejectedValue().toString() 
                                : null)
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("입력값 검증에 실패했습니다.")
                .fieldErrors(fieldErrors)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Validation error: {}", fieldErrors);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
    
    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("ILLEGAL_ARGUMENT")
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
    
    /**
     * IllegalStateException 처리
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("ILLEGAL_STATE")
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Illegal state: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }
    
    /**
     * 모든 예외 처리 (최종 fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        
        log.error("Unexpected error occurred", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("서버 내부 오류가 발생했습니다.")
                .detail(ex.getMessage())  // 개발 환경에서만 노출되도록 설정 가능
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}

