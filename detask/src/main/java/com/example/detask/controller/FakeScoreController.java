package com.example.detask.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/team-scores")
public class FakeScoreController {

    private final JdbcTemplate jdbc;

    private static final int SURVEY_POINTS = 1;
    private static final int ATTEND_POINTS = 5;

    // Change schema here if your table is not under dbo
    private static final String TABLE = "dbo.Points";

    public FakeScoreController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        // optional: ensure table exists (SQL Server)
        // jdbc.execute("IF OBJECT_ID('" + TABLE + "', 'U') IS NULL " +
        //              "BEGIN CREATE TABLE " + TABLE + " (ID BIGINT PRIMARY KEY, Points BIGINT NOT NULL DEFAULT 0) END");
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
            return ResponseEntity.status(500).body(Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage()));
        }
    }

    /** READ ONE */
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
            return ResponseEntity.status(500).body(Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage()));
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

    // Helper UPSERT
    private void addPoints(int teamId, int points) {
        int updated = jdbc.update("UPDATE " + TABLE + " SET Points = Points + ? WHERE ID = ?", points, teamId);
        if (updated == 0) {
            jdbc.update("INSERT INTO " + TABLE + " (ID, Points) VALUES (?, ?)", teamId, points);
        }
    }
}