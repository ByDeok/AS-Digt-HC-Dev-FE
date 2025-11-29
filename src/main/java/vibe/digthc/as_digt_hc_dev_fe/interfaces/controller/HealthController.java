package vibe.digthc.as_digt_hc_dev_fe.interfaces.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vibe.digthc.as_digt_hc_dev_fe.interfaces.common.ApiResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Health Check 컨트롤러
 */
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {
    
    /**
     * 서버 상태 확인
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "AS-Digt-HC Backend");
        
        return ResponseEntity.ok(ApiResponse.success("서버가 정상적으로 동작 중입니다.", status));
    }
}

