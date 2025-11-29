package vibe.digthc.as_digt_hc_dev_fe.domain.user.entity;

import vibe.digthc.as_digt_hc_dev_fe.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_profiles", indexes = {
    @Index(name = "idx_profiles_age_gender", columnList = "age, gender")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 255)
    private String profileImageUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private LocalDate birthDate;

    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(name = "primary_conditions", columnDefinition = "JSON")
    private String primaryConditions;

    @Column(name = "accessibility_prefs", columnDefinition = "JSON")
    private String accessibilityPrefs;

    @Builder
    public UserProfile(User user, String name, String phoneNumber, String profileImageUrl, LocalDate birthDate, Gender gender, Integer age) {
        this.user = user;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.birthDate = birthDate;
        this.gender = gender;
        this.age = age;
    }
    
    public void updateProfile(String name, String phoneNumber, String bio) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
    }
}
