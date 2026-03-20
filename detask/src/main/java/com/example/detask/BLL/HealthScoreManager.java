package com.example.detask.BLL;

import com.example.detask.BE.HealthScore;
import com.example.detask.BE.TeamHealthAnalytics;
import com.example.detask.DLL.HealthScoreDAO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class HealthScoreManager {

    private final HealthScoreDAO repo;

    // Each survey has 5 questions scored 1–5, so min=5, max=25
    private static final int MIN_SCORE = 5;
    private static final int MAX_SCORE = 25;

    // k-anonymity threshold — hide results for groups smaller than this
    private static final int MIN_GROUP_SIZE = 3;

    public HealthScoreManager(HealthScoreDAO repo) {
        this.repo = repo;
    }

    /**
     * Called by Chrome extension when a worker submits a survey.
     * answers = list of 5 integers (each 1–5), one per question.
     * Returns the saved HealthScore or null if validation fails.
     */
    public HealthScore submitSurvey(int userId, int teamId, int surveyId, List<Integer> answers) {
        // Validate
        if (userId <= 0 || teamId <= 0) return null;
        if (surveyId < 1 || surveyId > 3) return null;
        if (answers == null || answers.size() != 5) return null;
        for (int a : answers) {
            if (a < 1 || a > 5) return null;
        }

        int total = answers.stream().mapToInt(Integer::intValue).sum(); // 5–25

        HealthScore h = new HealthScore(userId, teamId, surveyId, total, System.currentTimeMillis());
        return repo.save(h);
    }

    /**
     * Called by dashboard to get today's anonymized team health per survey.
     * Returns one TeamHealthAnalytics per survey (1, 2, 3).
     */
    public List<TeamHealthAnalytics> getTeamHealthToday(int teamId) {
        long startOfToday = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli();

        List<TeamHealthAnalytics> results = new ArrayList<>();

        for (int surveyId = 1; surveyId <= 3; surveyId++) {
            List<HealthScore> scores = repo.findByTeamAndSurvey(teamId, surveyId, startOfToday);

            if (scores.isEmpty()) {
                results.add(new TeamHealthAnalytics(teamId, surveyId, 0, 0));
                continue;
            }

            int sampleSize = scores.size();

            // Anonymize small groups — don't reveal score if too few people answered
            if (sampleSize < MIN_GROUP_SIZE) {
                results.add(new TeamHealthAnalytics(teamId, surveyId, -1, sampleSize));
                continue;
            }

            double avg = scores.stream().mapToInt(HealthScore::getScore).average().orElse(0);
            int percent = toPercent(avg);

            results.add(new TeamHealthAnalytics(teamId, surveyId, percent, sampleSize));
        }

        return results;
    }

    /**
     * Converts a raw score (5–25) to a percentage (0–100).
     * score=5  → 0%
     * score=25 → 100%
     */
    private int toPercent(double score) {
        double normalized = (score - MIN_SCORE) / (double)(MAX_SCORE - MIN_SCORE);
        return (int) Math.round(normalized * 100);
    }
}
