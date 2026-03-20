package com.example.detask.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/team-scores")
public class FakeScoreController {

    private final JdbcTemplate jdbc;

    // constants: +1 for survey, +5 for attend
    private static final int SURVEY_POINTS = 1;
    private static final int ATTEND_POINTS = 5;

    public FakeScoreController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * POST /api/v1/team-scores/survey
     * Body: { "teamId": 1 }
     */
    @PostMapping("/survey")
    public ResponseEntity<?> addSurveyPoints(@RequestBody Map<String, Integer> body) {
        int teamId = body.getOrDefault("teamId", 1);
        addPoints(teamId, SURVEY_POINTS);
        return ResponseEntity.ok(Map.of("teamId", teamId, "added", SURVEY_POINTS));
    }

    /**
     * POST /api/v1/team-scores/attend
     * Body: { "teamId": 1 }
     */
    @PostMapping("/attend")
    public ResponseEntity<?> addAttendPoints(@RequestBody Map<String, Integer> body) {
        int teamId = body.getOrDefault("teamId", 1);
        addPoints(teamId, ATTEND_POINTS);
        return ResponseEntity.ok(Map.of("teamId", teamId, "added", ATTEND_POINTS));
    }

    /**
     * GET /api/v1/team-scores/{teamId}
     * Convenience endpoint to read current points (handy for testing from the browser).
     */
    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeamPoints(@PathVariable int teamId) {
        try {
            // If your table/columns were created quoted and case-sensitive, swap to the quoted SQL below.
            Integer points = jdbc.query(
                    "SELECT COALESCE(Points, 0) AS Points FROM TeamScore WHERE ID = ?",
                    rs -> rs.next() ? rs.getInt("Points") : 0,
                    teamId
            );
            return ResponseEntity.ok(Map.of("teamId", teamId, "points", points));
        } catch (Exception e) {
            // TEMP: show the DB error so you can see the exact cause in the response while debugging
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage()));
        }
    }

    // helper: UPSERT-like logic
    private void addPoints(int teamId, int points) {
        int updated = jdbc.update("UPDATE TeamScore SET Points = Points + ? WHERE ID = ?", points, teamId);
        if (updated == 0) {
            jdbc.update("INSERT INTO TeamScore (ID, Points) VALUES (?, ?)", teamId, points);
        }
    }
}