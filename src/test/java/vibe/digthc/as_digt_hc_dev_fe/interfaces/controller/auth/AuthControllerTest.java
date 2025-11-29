package vibe.digthc.as_digt_hc_dev_fe.interfaces.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.dto.*;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.Role;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.service.AuthService;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.JwtAuthenticationFilter;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.SecurityConfig;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.JwtTokenProvider;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.CustomUserDetailsService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void signup_ShouldReturnSuccess() throws Exception {
        SignupRequest request = new SignupRequest(
                "test@example.com",
                "Password123!",
                "Test User",
                Role.SENIOR,
                new AgreementsRequest(true, true, true)
        );

        UserResponse response = new UserResponse(
                UUID.randomUUID(), "test@example.com", "Test User", Role.SENIOR, java.time.LocalDateTime.now()
        );

        given(authService.signup(any(SignupRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }
}

