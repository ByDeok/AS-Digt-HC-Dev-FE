package vibe.digthc.as_digt_hc_dev_fe.domain.action.entity;

import jakarta.persistence.*;
import lombok.*;
import vibe.digthc.as_digt_hc_dev_fe.domain.action.enums.ActionStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.common.BaseTimeEntity;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;

import java.time.LocalDate;

@Entity
@Table(name = "action_cards", indexes = {
    @Index(name = "idx_action_card_date_user", columnList = "action_date, user_id"),
    @Index(name = "idx_action_card_status", columnList = "status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActionCard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "action_date", nullable = false)
    private LocalDate actionDate;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActionStatus status;

    @Column(name = "rule_id")
    private String ruleId; // To track which rule generated this card

    @Builder
    public ActionCard(User user, LocalDate actionDate, String title, String description, ActionStatus status, String ruleId) {
        this.user = user;
        this.actionDate = actionDate;
        this.title = title;
        this.description = description;
        this.status = status != null ? status : ActionStatus.PENDING;
        this.ruleId = ruleId;
    }

    public void updateStatus(ActionStatus status) {
        this.status = status;
    }
}

