package vibe.digthc.as_digt_hc_dev_fe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * AS-Digt-HC Backend Application
 * 
 * @author AS-Digt-HC Team
 */
@SpringBootApplication
@EnableJpaAuditing  // JPA Auditing 활성화 (BaseTimeEntity 사용을 위해)
public class AsDigtHcDevFeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsDigtHcDevFeApplication.class, args);
	}

}
