package vibe.digthc.as_digt_hc_dev_fe.interfaces.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

import vibe.digthc.as_digt_hc_dev_fe.domain.onboarding.exception.UnsupportedRegionException;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.exception.BoardNotFoundException;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.exception.MemberNotFoundException;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.exception.IllegalOperationException;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.exception.InvalidInvitationException;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.exception.DeviceNotFoundException;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.exception.DeviceAlreadyLinkedException;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.exception.ConsentNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

/**
 * 전역 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiResponse<ErrorResponse>> buildErrorResponse(HttpStatus status, ErrorResponse errorResponse) {
        return ResponseEntity.status(status)
                .body(ApiResponse.failure(errorResponse.getMessage(), errorResponse));
    }


    /**
     * 정적 리소스/매핑 미존재(404) 처리
     * - 예: /h2-console 이 실제로 노출되지 않는 경우
     * - 기존에는 최종 fallback(Exception)에서 500으로 처리되어 원인 파악이 어려웠다.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleNoResourceFound(NoResourceFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("NOT_FOUND")
                .message("요청한 리소스를 찾을 수 없습니다.")
                .detail(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();

        log.warn("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, errorResponse);
    }

    /**
     * 파라미터 타입 변환 실패 처리 (400)
     * - 예: UUID가 필요한 PathVariable에 '2' 같은 값이 들어오는 경우
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("TYPE_MISMATCH")
                .message("요청 파라미터 형식이 올바르지 않습니다.")
                .detail(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();

        log.warn("Type mismatch: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorResponse);
    }
    
    /**
     * UnsupportedRegionException 처리
     */
    @ExceptionHandler(UnsupportedRegionException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUnsupportedRegionException(
            UnsupportedRegionException ex) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("UNSUPPORTED_REGION")
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Unsupported region: {}", ex.getMessage());
        
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorResponse);
    }

    /**
     * 필드 검증 예외 처리 (@Valid 실패)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleValidationException(
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
        
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorResponse);
    }
    
    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("ILLEGAL_ARGUMENT")
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorResponse);
    }
    
    /**
     * IllegalStateException 처리
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalStateException(
            IllegalStateException ex) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("ILLEGAL_STATE")
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Illegal state: {}", ex.getMessage());
        
        return buildErrorResponse(HttpStatus.CONFLICT, errorResponse);
    }
    
    /**
     * BoardNotFoundException 처리
     */
    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBoardNotFoundException(BoardNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("BOARD_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Board not found: {}", ex.getMessage());
        
        return buildErrorResponse(HttpStatus.NOT_FOUND, errorResponse);
    }

    /**
     * MemberNotFoundException 처리
     */
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMemberNotFoundException(MemberNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("MEMBER_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Member not found: {}", ex.getMessage());
        
        return buildErrorResponse(HttpStatus.NOT_FOUND, errorResponse);
    }

    /**
     * IllegalOperationException 처리
     */
    @ExceptionHandler(IllegalOperationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalOperationException(IllegalOperationException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("ILLEGAL_OPERATION")
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Illegal operation: {}", ex.getMessage());
        
        return buildErrorResponse(HttpStatus.FORBIDDEN, errorResponse);
    }

    /**
     * InvalidInvitationException 처리
     */
    @ExceptionHandler(InvalidInvitationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleInvalidInvitationException(InvalidInvitationException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INVALID_INVITATION")
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Invalid invitation: {}", ex.getMessage());
        
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorResponse);
    }

    /**
     * DeviceNotFoundException 처리
     */
    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDeviceNotFoundException(DeviceNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("DEVICE_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Device not found: {}", ex.getMessage());
        
        return buildErrorResponse(HttpStatus.NOT_FOUND, errorResponse);
    }

    /**
     * DeviceAlreadyLinkedException 처리
     */
    @ExceptionHandler(DeviceAlreadyLinkedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDeviceAlreadyLinkedException(DeviceAlreadyLinkedException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("DEVICE_ALREADY_LINKED")
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Device already linked: {}", ex.getMessage());
        
        return buildErrorResponse(HttpStatus.CONFLICT, errorResponse);
    }

    /**
     * ConsentNotFoundException 처리
     */
    @ExceptionHandler(ConsentNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleConsentNotFoundException(ConsentNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("CONSENT_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Consent not found: {}", ex.getMessage());
        
        return buildErrorResponse(HttpStatus.NOT_FOUND, errorResponse);
    }

    /**
     * SecurityException 처리
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleSecurityException(SecurityException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("SECURITY_ERROR")
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Security error: {}", ex.getMessage());
        
        return buildErrorResponse(HttpStatus.FORBIDDEN, errorResponse);
    }

    /**
     * AuthenticationException 처리 (인증 실패)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("AUTHENTICATION_FAILED")
                .message("인증에 실패했습니다.")
                .detail(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Authentication failed: {}", ex.getMessage());
        
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, errorResponse);
    }

    /**
     * AccessDeniedException 처리 (권한 부족)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("ACCESS_DENIED")
                .message("접근 권한이 없습니다.")
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        log.warn("Access denied: {}", ex.getMessage());
        
        return buildErrorResponse(HttpStatus.FORBIDDEN, errorResponse);
    }

    /**
     * 모든 예외 처리 (최종 fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleException(Exception ex) {
        
        log.error("Unexpected error occurred", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("서버 내부 오류가 발생했습니다.")
                .detail(ex.getMessage())  // 개발 환경에서만 노출되도록 설정 가능
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse);
    }
}

