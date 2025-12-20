package vibe.digthc.as_digt_hc_dev_fe.domain.report.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vibe.digthc.as_digt_hc_dev_fe.domain.report.dto.ReportMetrics;

@Converter
public class ReportMetricsConverter implements AttributeConverter<ReportMetrics, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ReportMetrics attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting ReportMetrics to JSON", e);
        }
    }

    @Override
    public ReportMetrics convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, ReportMetrics.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to ReportMetrics", e);
        }
    }
}

