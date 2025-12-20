package vibe.digthc.as_digt_hc_dev_fe.domain.family.entity;

import vibe.digthc.as_digt_hc_dev_fe.domain.common.BaseTimeEntity;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 가족 보드 Entity
 * - 시니어와 보호자 간 데이터 공유 공간
 */
@Entity
@Table(name = "family_boards",
    indexes = {
        @Index(name = "idx_boards_senior", columnList = "senior_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FamilyBoard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false, unique = true)
    private User senior;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private Map<String, Object> settings;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FamilyBoardMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardInvitation> invitations = new ArrayList<>();

    // ========================================
    // Builder
    // ========================================
    @Builder
    private FamilyBoard(User senior, String name, String description) {
        this.senior = senior;
        this.name = name;
        this.description = description;
        this.settings = getDefaultSettings();
        this.lastActivityAt = LocalDateTime.now();
    }

    // ========================================
    // Factory Method
    // ========================================
    public static FamilyBoard create(User senior) {
        String seniorName = senior.getUserProfile() != null ? senior.getUserProfile().getName() : "시니어";
        return FamilyBoard.builder()
                .senior(senior)
                .name(seniorName + "님의 가족 보드")
                .description("가족과 함께 건강 정보를 공유하세요")
                .build();
    }

    // ========================================
    // Business Methods
    // ========================================

    /**
     * 설정 업데이트
     */
    public void updateSettings(Map<String, Object> newSettings) {
        this.settings = newSettings;
        updateLastActivity();
    }

    /**
     * 마지막 활동 시간 갱신
     */
    public void updateLastActivity() {
        this.lastActivityAt = LocalDateTime.now();
    }

    /**
     * 보드명 변경
     */
    public void updateName(String name) {
        this.name = name;
        updateLastActivity();
    }

    /**
     * 활성 멤버 수 조회
     */
    public int getActiveMemberCount() {
        return (int) members.stream()
                .filter(FamilyBoardMember::isActive)
                .count();
    }

    // ========================================
    // Private Methods
    // ========================================

    private Map<String, Object> getDefaultSettings() {
        return Map.of(
            "notifications", Map.of(
                "emergencyAlerts", true,
                "medicationReminders", true,
                "activityUpdates", true
            ),
            "privacy", Map.of(
                "shareHealthData", true,
                "shareActivityLog", true
            )
        );
    }
}

































