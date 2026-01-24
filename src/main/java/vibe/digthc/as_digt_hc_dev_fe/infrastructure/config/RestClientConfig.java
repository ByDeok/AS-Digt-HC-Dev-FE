package vibe.digthc.as_digt_hc_dev_fe.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * REST 클라이언트 설정 클래스
 *
 * 외부 서비스 호출을 위한 RestTemplate 빈을 정의합니다.
 * - GA4 Measurement Protocol API 호출
 * - 기타 외부 API 연동
 */
@Configuration
public class RestClientConfig {

    /**
     * 기본 RestTemplate 빈
     * - Connection Timeout: 5초
     * - Read Timeout: 10초
     *
     * @return RestTemplate 인스턴스
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5초
        factory.setReadTimeout(10000); // 10초
        return new RestTemplate(factory);
    }

    /**
     * Analytics 전용 비동기 RestTemplate
     * - 짧은 타임아웃 설정 (이벤트 전송 실패가 서비스에 영향을 주지 않도록)
     * - Connection Timeout: 2초
     * - Read Timeout: 5초
     *
     * @return RestTemplate 인스턴스
     */
    @Bean(name = "analyticsRestTemplate")
    public RestTemplate analyticsRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000); // 2초
        factory.setReadTimeout(5000); // 5초
        return new RestTemplate(factory);
    }
}
