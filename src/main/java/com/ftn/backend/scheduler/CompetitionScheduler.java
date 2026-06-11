package com.ftn.backend.scheduler;

import com.ftn.backend.service.CompetitionService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Runs every hour to auto-transition competition statuses based on today's date.
 *
 * Algorithm:
 *  1. PLANIFIEE → EN_COURS  when startDate <= today <= endDate
 *  2. EN_COURS  → TERMINEE  when today > endDate
 *
 * TERMINEE competitions are hidden from the main calendar list (filtered on frontend).
 * Their results remain accessible in the Résultats module.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CompetitionScheduler {

    private final CompetitionService competitionService;

    @Scheduled(cron = "0 0 * * * *") // every hour at minute 0
    public void updateCompetitionStatuses() {
        LocalDate today = LocalDate.now();
        log.info("CompetitionScheduler: checking statuses for {}", today);
        competitionService.updateStatusesForDate(today);
    }
}
