package vibe.digthc.as_digt_hc_dev_fe.interfaces.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Welcome Controller - 루트 경로 처리
 * context-path(/api) 외부에서 루트 접근 시 안내 메시지 제공
 */
@Controller
public class WelcomeController {

    /**
     * 루트 경로 접근 시 API 정보 안내
     */
    @GetMapping("/")
    @ResponseBody
    public Map<String, Object> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "AS-Digt-HC Backend");
        response.put("status", "running");
        response.put("message", "Welcome! API endpoints are available at /api/*");
        response.put("endpoints", Map.of(
                "health", "/api/health",
                "auth", "/api/auth/*",
                "user", "/api/users/*",
                "h2-console", "/h2-console (local only)"));
        return response;
    }
}
