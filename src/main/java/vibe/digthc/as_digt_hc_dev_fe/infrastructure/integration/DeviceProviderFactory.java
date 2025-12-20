package vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 디바이스 Provider 팩토리
 */
@Component
@RequiredArgsConstructor
public class DeviceProviderFactory {

    private final List<DeviceDataProvider> providers;
    private Map<String, DeviceDataProvider> providerMap;

    /**
     * 벤더별 Provider 조회
     */
    public DeviceDataProvider getProvider(String vendor) {
        if (providerMap == null) {
            providerMap = providers.stream()
                    .collect(Collectors.toMap(DeviceDataProvider::getVendor, Function.identity()));
        }
        
        DeviceDataProvider provider = providerMap.get(vendor);
        if (provider == null) {
            throw new IllegalArgumentException("지원하지 않는 디바이스 벤더입니다: " + vendor);
        }
        return provider;
    }
}

