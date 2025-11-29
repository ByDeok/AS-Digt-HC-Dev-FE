package vibe.digthc.as_digt_hc_dev_fe.domain.action.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vibe.digthc.as_digt_hc_dev_fe.domain.action.entity.ActionCard;
import vibe.digthc.as_digt_hc_dev_fe.domain.action.enums.ActionStatus;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionCardResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate actionDate;
    private ActionStatus status;
    private String ruleId;

    public static ActionCardResponse from(ActionCard entity) {
        return ActionCardResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .actionDate(entity.getActionDate())
                .status(entity.getStatus())
                .ruleId(entity.getRuleId())
                .build();
    }
}

