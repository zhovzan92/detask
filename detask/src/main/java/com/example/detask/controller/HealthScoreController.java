package com.example.detask.controller;

import com.example.detask.BE.HealthScore;
import com.example.detask.BE.TeamHealthAnalytics;
import com.example.detask.BLL.HealthScoreManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthScoreController {

    private final HealthScoreManager manager;

    public HealthScoreController(HealthScoreManager manager) {
        this.manager = manager;
    }

    /**
     * Called by Chrome extension when worker submits a survey.
     *
     * POST /api/v1/health/submit
     * Body: {
     *   "userId": 1,
     *   "teamId": 1,
     *   "surveyId": 1,
     *   "answers": [4, 3, 5, 2, 4]   ← 5 answers, each 1–5
     * }
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitSurvey(@RequestBody Map<String, Object> body) {
        try {
            int userId   = (int) body.get("userId");
            int teamId   = (int) body.get("teamId");
            int surveyId = (int) body.get("surveyId");

            @SuppressWarnings("unchecked")
            List<Integer> answers = (List<Integer>) body.get("answers");

            HealthScore saved = manager.submitSurvey(userId, teamId, surveyId, answers);

            if (saved == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "VALIDATION_ERROR",
                                "message", "Invalid input. Need surveyId 1–3 and exactly 5 answers (1–5)."));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Survey submitted",
                    "scoreId", saved.getId(),
                    "score", saved.getScore()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    /**
     * Called by dashboard to get today's anonymized team health scores.
     *
     * GET /api/v1/health/team/1
     * Returns one entry per survey (1, 2, 3):
     * [
     *   { "teamId": 1, "surveyId": 1, "averageScorePercent": 72, "sampleSize": 5 },
     *   { "teamId": 1, "surveyId": 2, "averageScorePercent": -1, "sampleSize": 2 }, ← anonymized
     *   { "teamId": 1, "surveyId": 3, "averageScorePercent": 0,  "sampleSize": 0 }  ← no data
     * ]
     */
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TeamHealthAnalytics>> getTeamHealth(@PathVariable int teamId) {
        return ResponseEntity.ok(manager.getTeamHealthToday(teamId));
    }
}