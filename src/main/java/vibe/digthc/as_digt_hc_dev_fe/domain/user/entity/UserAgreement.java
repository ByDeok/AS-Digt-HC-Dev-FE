package vibe.digthc.as_digt_hc_dev_fe.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_agreements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean termsService;

    @Column(nullable = false)
    private boolean privacyPolicy;

    @Column(nullable = false)
    private boolean marketingConsent;

    @Column(nullable = false)
    private LocalDateTime agreedAt;

    @Builder
    public UserAgreement(User user, boolean termsService, boolean privacyPolicy, boolean marketingConsent) {
        this.user = user;
        this.termsService = termsService;
        this.privacyPolicy = privacyPolicy;
        this.marketingConsent = marketingConsent;
        this.agreedAt = LocalDateTime.now();
    }
}

