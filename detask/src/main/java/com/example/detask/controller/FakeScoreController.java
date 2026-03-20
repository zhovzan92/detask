package com.example.scores;

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
// Uncomment and adjust if your frontend runs on a different origin (e.g., Vite/React on :5173)
// @CrossOrigin(origins = {"http://localhost:3000","http://localhost:5173"})
public class FakeScoreController {

    private final JdbcTemplate jdbc;

    // Scoring rules
    private static final int SURVEY_POINTS = 1;  // +1 per survey
    private static final int ATTEND_POINTS = 5;  // +5 per attendance

    // Change schema/table if yours differs
    private static final String TABLE = "dbo.Points";

    public FakeScoreController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;

        // OPTIONAL: Ensure table exists (SQL Server). Commented out by default.
        // jdbc.execute("""
        //     IF OBJECT_ID('""" + TABLE + """', 'U') IS NULL
        //     BEGIN
        //         CREATE TABLE """ + TABLE + """ (
        //             ID BIGINT PRIMARY KEY,
        //             Points BIGINT NOT NULL DEFAULT 0
        //         )
        //     END
        // """);
    }

    /** LIST ALL: [{ teamId, points }, ...] */
    @GetMapping
    public ResponseEntity<?> getAllTeamScores() {
        try {
            List<Map<String, Object>> rows = jdbc.query(
                    "SELECT ID, Points FROM " + TABLE + " ORDER BY ID ASC",
                    (rs, i) -> Map.of(
                            "teamId", rs.getLong("ID"),
                            "points", rs.getLong("Points"))
            );
            return ResponseEntity.ok(rows);
        } catch (Exception e) {
            return serverError(e);
        }
    }

    /** READ ONE: { teamId, points } */
    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeamPoints(@PathVariable long teamId) {
        try {
            Long points = jdbc.query(
                    "SELECT COALESCE(Points, 0) AS Points FROM " + TABLE + " WHERE ID = ?",
                    rs -> rs.next() ? rs.getLong("Points") : 0L,
                    teamId
            );
            return ResponseEntity.ok(Map.of("teamId", teamId, "points", points));
        } catch (Exception e) {
            return serverError(e);
        }
    }

    /** TOTAL ACROSS ALL TEAMS: { totalPoints } */
    @GetMapping("/total")
    public ResponseEntity<?> getTotalPoints() {
        try {
            Long total = jdbc.queryForObject(
                    "SELECT COALESCE(SUM(Points), 0) FROM " + TABLE,
                    Long.class
            );
            return ResponseEntity.ok(Map.of("totalPoints", total != null ? total : 0L));
        } catch (Exception e) {
            return serverError(e);
        }
    }

    /** +1 for survey: body { "teamId": number } (teamId defaults to 1) */
    @PostMapping("/survey")
    public ResponseEntity<?> addSurveyPoints(@RequestBody Map<String, Object> body) {
        try {
            long teamId = getTeamIdOrDefault(body, 1L);
            addPoints(teamId, SURVEY_POINTS);
            return ResponseEntity.ok(Map.of("teamId", teamId, "added", SURVEY_POINTS));
        } catch (Exception e) {
            return serverError(e);
        }
    }

    /** +5 for attend: body { "teamId": number } (teamId defaults to 1) */
    @PostMapping("/attend")
    public ResponseEntity<?> addAttendPoints(@RequestBody Map<String, Object> body) {
        try {
            long teamId = getTeamIdOrDefault(body, 1L);
            addPoints(teamId, ATTEND_POINTS);
            return ResponseEntity.ok(Map.of("teamId", teamId, "added", ATTEND_POINTS));
        } catch (Exception e) {
            return serverError(e);
        }
    }

    // ---------------------------
    // Helpers
    // ---------------------------

    /** UPSERT add: try UPDATE; if 0 rows updated, INSERT a new row */
    private void addPoints(long teamId, int points) {
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

    /** Extract teamId from body safely (supports number types) */
    private long getTeamIdOrDefault(Map<String, Object> body, long def) {
        if (body == null) return def;
        Object raw = body.get("teamId");
        if (raw == null) return def;
        if (raw instanceof Number n) return n.longValue();
        try {
            return Long.parseLong(String.valueOf(raw));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private ResponseEntity<Map<String, String>> serverError(Exception e) {
        return ResponseEntity.status(500).body(Map.of(
                "error", e.getClass().getSimpleName(),
                "message", e.getMessage() != null ? e.getMessage() : "Internal Server Error"
        ));
    }
}