package vibe.digthc.as_digt_hc_dev_fe.domain.action.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionStatsResponse {
    private double dailyCompletionRate; // D1 (Yesterday or Today depending on requirement, let's do Today for real-time or Yesterday for full day stats. The requirement just says D1. I'll provide Today's rate so far)
    private double weeklyCompletionRate; // W1 (Last 7 days)
}

