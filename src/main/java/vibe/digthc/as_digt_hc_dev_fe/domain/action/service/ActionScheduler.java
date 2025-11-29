package vibe.digthc.as_digt_hc_dev_fe.domain.action.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActionScheduler {

    private final ActionCardService actionCardService;

    // 매일 자정(00:00:00)에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void runDailyGeneration() {
        log.info("Starting daily action card generation...");
        try {
            actionCardService.generateDailyCards();
            log.info("Daily action card generation completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred during daily action card generation", e);
        }
    }
}

