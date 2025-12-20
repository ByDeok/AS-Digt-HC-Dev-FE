package vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 포털 Provider 팩토리
 */
@Component
@RequiredArgsConstructor
public class PortalProviderFactory {

    private final List<PortalDataProvider> providers;
    private Map<String, PortalDataProvider> providerMap;

    /**
     * 포털 타입별 Provider 조회
     */
    public PortalDataProvider getProvider(String portalType) {
        if (providerMap == null) {
            providerMap = providers.stream()
                    .collect(Collectors.toMap(PortalDataProvider::getPortalType, Function.identity()));
        }
        
        PortalDataProvider provider = providerMap.get(portalType);
        if (provider == null) {
            throw new IllegalArgumentException("지원하지 않는 포털 타입입니다: " + portalType);
        }
        return provider;
    }
}

