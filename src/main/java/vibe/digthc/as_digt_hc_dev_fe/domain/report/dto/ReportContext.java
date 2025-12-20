package vibe.digthc.as_digt_hc_dev_fe.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportContext {
    private String deviceId;
    private String deviceType; // e.g. "WATCH", "SCALE", "PORTAL"
    private boolean isMissingData;
    private List<String> missingDataFields;
    private String metadata; // Extra metadata if needed
}

