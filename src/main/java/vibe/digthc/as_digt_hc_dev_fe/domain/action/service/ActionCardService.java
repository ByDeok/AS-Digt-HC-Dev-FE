package vibe.digthc.as_digt_hc_dev_fe.domain.action.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.digthc.as_digt_hc_dev_fe.domain.action.dto.ActionCardResponse;
import vibe.digthc.as_digt_hc_dev_fe.domain.action.dto.ActionStatsResponse;
import vibe.digthc.as_digt_hc_dev_fe.domain.action.entity.ActionCard;
import vibe.digthc.as_digt_hc_dev_fe.domain.action.enums.ActionStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.action.repository.ActionCardRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActionCardService {

    private final ActionCardRepository actionCardRepository;
    private final UserRepository userRepository;

    public List<ActionCardResponse> getTodayActions(UUID userId) {
        User user = getUser(userId);
        LocalDate today = LocalDate.now();
        return actionCardRepository.findByUserAndActionDate(user, today).stream()
                .map(ActionCardResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ActionCardResponse completeAction(Long actionId, UUID userId) {
        User user = getUser(userId);
        ActionCard actionCard = actionCardRepository.findById(actionId)
                .orElseThrow(() -> new IllegalArgumentException("Action card not found"));

        if (!actionCard.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You do not have permission to access this card");
        }

        actionCard.updateStatus(ActionStatus.COMPLETED);
        return ActionCardResponse.from(actionCard);
    }

    @Transactional
    public void generateDailyCards() {
        // Find all active users (this might be heavy for real prod, but okay for MVP)
        List<User> users = userRepository.findAll(); // In real scenario, use batch processing or paging
        LocalDate today = LocalDate.now();

        for (User user : users) {
            if (actionCardRepository.existsByUserAndActionDate(user, today)) {
                continue; // Already generated
            }
            generateCardsForUser(user, today);
        }
    }

    @Transactional
    public void generateCardsForUser(User user, LocalDate date) {
        // Simple Rule-based Logic
        // 1. Check latest health report
        // Note: Ideally we fetch report for yesterday, but here we just pick the latest one for simplicity
        // or generate a default if no report exists.
        
        List<ActionCard> cards = new ArrayList<>();

        // Default card
        cards.add(ActionCard.builder()
                .user(user)
                .actionDate(date)
                .title("물 마시기")
                .description("하루 8잔의 물을 마셔보세요.")
                .status(ActionStatus.PENDING)
                .ruleId("RULE_HYDRATION")
                .build());

        // Check Steps from HealthReport
        // Since I cannot easily get "Yesterday's report" without more complex logic, 
        // I will just query the latest report for now, or skip if none.
        // For MVP, let's assume we want to encourage walking if no data or low data.
        
        cards.add(ActionCard.builder()
                .user(user)
                .actionDate(date)
                .title("가벼운 산책하기")
                .description("30분 정도 가볍게 걸어보세요.")
                .status(ActionStatus.PENDING)
                .ruleId("RULE_WALKING")
                .build());

        // Save all
        actionCardRepository.saveAll(cards);
    }

    public ActionStatsResponse getStatistics(UUID userId) {
        User user = getUser(userId);
        LocalDate today = LocalDate.now();
        
        // D1: Today's completion rate
        long todayTotal = actionCardRepository.countByUserAndDateRange(user, today, today);
        long todayCompleted = actionCardRepository.countByUserAndStatusAndDateRange(user, ActionStatus.COMPLETED, today, today);
        double dailyRate = todayTotal == 0 ? 0.0 : (double) todayCompleted / todayTotal * 100.0;

        // W1: Last 7 days (inclusive of today? or strictly last 7 days? Let's do last 7 days including today)
        LocalDate oneWeekAgo = today.minusDays(6);
        long weekTotal = actionCardRepository.countByUserAndDateRange(user, oneWeekAgo, today);
        long weekCompleted = actionCardRepository.countByUserAndStatusAndDateRange(user, ActionStatus.COMPLETED, oneWeekAgo, today);
        double weeklyRate = weekTotal == 0 ? 0.0 : (double) weekCompleted / weekTotal * 100.0;

        return ActionStatsResponse.builder()
                .dailyCompletionRate(dailyRate)
                .weeklyCompletionRate(weeklyRate)
                .build();
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}

