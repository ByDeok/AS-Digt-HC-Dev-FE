package vibe.digthc.as_digt_hc_dev_fe.interfaces.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.dto.AgreementsRequest;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.dto.SignupRequest;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.dto.UserResponse;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.Role;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.service.AuthService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

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

        mockMvc.perform(post("/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }
}
