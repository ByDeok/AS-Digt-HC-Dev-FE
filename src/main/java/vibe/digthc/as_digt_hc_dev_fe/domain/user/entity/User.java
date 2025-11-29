package vibe.digthc.as_digt_hc_dev_fe.domain.user.entity;

import vibe.digthc.as_digt_hc_dev_fe.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_role_status", columnList = "role, status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AuthProvider authProvider;

    @Column(length = 255)
    private String providerId;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile;

    @Builder
    public User(String email, String password, Role role, Status status, AuthProvider authProvider, String providerId) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status != null ? status : Status.ACTIVE;
        this.authProvider = authProvider;
        this.providerId = providerId;
    }
    
    public void updateStatus(Status status) {
        this.status = status;
    }
}
