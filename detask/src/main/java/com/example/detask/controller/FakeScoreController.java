package com.example.detask.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API for team scores backed by a simple (ID, Points) table.
 *
 * Endpoints:
 *  GET  /api/v1/team-scores            -> list all teams {teamId, points}
 *  GET  /api/v1/team-scores/{teamId}   -> get one team's points
 *  GET  /api/v1/team-scores/total      -> total points across all teams
 *  POST /api/v1/team-scores/survey     -> add +1 point to a team
 *  POST /api/v1/team-scores/attend     -> add +5 points to a team
 */
@RestController
@RequestMapping("/api/v1/team-scores")
public class FakeScoreController {

    private final JdbcTemplate jdbc;

    private static final int SURVEY_POINTS = 1;
    private static final int ATTEND_POINTS = 5;
    private static final String TABLE = "dbo.Points";

    public FakeScoreController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** LIST ALL */
    @GetMapping
    public ResponseEntity<?> getAllTeamScores() {
        try {
            var rows = jdbc.query(
                    "SELECT ID, Points FROM " + TABLE + " ORDER BY ID ASC",
                    (rs, i) -> Map.of("teamId", rs.getInt("ID"), "points", rs.getInt("Points"))
            );
            return ResponseEntity.ok(rows);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /** TOTAL ACROSS ALL TEAMS */
    @GetMapping("/total")
    public ResponseEntity<?> getTotalPoints() {
        try {
            Long total = jdbc.queryForObject(
                    "SELECT COALESCE(SUM(Points),0) FROM " + TABLE,
                    Long.class
            );
            return ResponseEntity.ok(Map.of("totalPoints", total));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /** +1 for survey */
    @PostMapping("/survey")
    public ResponseEntity<?> addSurveyPoints(@RequestBody Map<String, Integer> body) {
        int teamId = body.getOrDefault("teamId", 1);
        addPoints(teamId, SURVEY_POINTS);
        return ResponseEntity.ok(Map.of("teamId", teamId, "added", SURVEY_POINTS));
    }

    /** +5 for attend */
    @PostMapping("/attend")
    public ResponseEntity<?> addAttendPoints(@RequestBody Map<String, Integer> body) {
        int teamId = body.getOrDefault("teamId", 1);
        addPoints(teamId, ATTEND_POINTS);
        return ResponseEntity.ok(Map.of("teamId", teamId, "added", ATTEND_POINTS));
    }

    /** READ ONE (must be LAST) */
    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeamPoints(@PathVariable int teamId) {
        try {
            Integer points = jdbc.query(
                    "SELECT COALESCE(Points, 0) AS Points FROM " + TABLE + " WHERE ID = ?",
                    rs -> rs.next() ? rs.getInt("Points") : 0,
                    teamId
            );
            return ResponseEntity.ok(Map.of("teamId", teamId, "points", points));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /** Helper UPSERT */
    private void addPoints(int teamId, int points) {
        int updated = jdbc.update(
                "UPDATE " + TABLE + " SET Points = Points + ? WHERE ID = ?",
                points, teamId
        );
        if (updated == 0) {
            jdbc.update(
                    "INSERT INTO " + TABLE + " (ID, Points) VALUES (?, ?)",
                    teamId, points
            );
        }
    }
}