package vibe.digthc.as_digt_hc_dev_fe.domain.family.dto;

import java.util.Map;

public record SettingsUpdateReq(
    Map<String, Object> settings
) {}

