package vibe.digthc.as_digt_hc_dev_fe.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import vibe.digthc.as_digt_hc_dev_fe.interfaces.common.ApiResponse;
import vibe.digthc.as_digt_hc_dev_fe.interfaces.common.ErrorResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.cors.allowed-origin-patterns:http://localhost:*,http://127.0.0.1:*}")
    private String allowedOriginPatterns;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // H2 콘솔용
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) ->
                                writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED", "인증에 실패했습니다."))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeErrorResponse(response, HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근 권한이 없습니다.")))
                .authorizeHttpRequests(auth -> auth
                        // 공개 엔드포인트
                        .requestMatchers("/").permitAll() // 루트 경로
                        // server.servlet.context-path=/api 이므로, 매칭은 보통 컨텍스트 패스를 제외한 경로(/health) 기준으로 동작
                        .requestMatchers("/health").permitAll() // 헬스체크
                        .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 (로컬 개발용)
                        .requestMatchers("/v1/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 인증 필요 엔드포인트
                        .requestMatchers("/v1/users/**").authenticated()
                        .requestMatchers("/onboarding/**").authenticated()
                        .requestMatchers("/reports/**").authenticated()
                        .requestMatchers("/actions/**").authenticated()
                        .requestMatchers("/v1/family-board/**").authenticated()
                        .requestMatchers("/v1/integration/**").authenticated()
                        // 관리자 전용 엔드포인트 (추후 확장 가능)
                        .requestMatchers("/v1/admin/**").hasRole("ADMIN")
                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 개발 환경용 CORS 설정
     * - FE(예: Vite dev server: http://localhost:9002)에서 BE로 직접 호출할 때
     *   preflight(OPTIONS)가 403으로 막히는 문제를 방지한다.
     *
     * 참고:
     * - Vite proxy(`/api -> BE`)를 쓰면 CORS가 필요 없지만,
     *   팀 환경마다 baseURL을 직접 BE로 지정하는 경우가 있어 안전하게 허용한다.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOriginPatterns.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
        config.setAllowedOriginPatterns(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String code, String message)
            throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
        ApiResponse<ErrorResponse> body = ApiResponse.failure(message, errorResponse);
        response.setStatus(status.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
