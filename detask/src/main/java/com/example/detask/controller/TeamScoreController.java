package com.example.detask.controller;

import com.example.detask.BE.TeamScore;
import com.example.detask.BLL.TeamScoreManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/score")
public class TeamScoreController {

    private final TeamScoreManager manager;

    public TeamScoreController(TeamScoreManager manager) {
        this.manager = manager;
    }

    /**
     * Called by Chrome extension when worker finishes all 3 daily surveys.
     * Adds 1 point — answers are NOT sent here, only completion.
     *
     * POST /api/v1/score/survey
     * Body: { "userId": 1, "teamId": 1 }
     */
    @PostMapping("/survey")
    public ResponseEntity<?> completeSurvey(@RequestBody Map<String, Integer> body) {
        int userId = body.get("userId");
        int teamId = body.get("teamId");
        manager.recordSurveyCompletion(userId, teamId);
        return ResponseEntity.ok(Map.of("message", "Survey completion recorded", "points", 1));
    }

    /**
     * Called by Chrome extension when worker presses "Attend".
     * Adds 5 points.
     *
     * POST /api/v1/score/attend
     * Body: { "userId": 1, "teamId": 1 }
     */
    @PostMapping("/attend")
    public ResponseEntity<?> attend(@RequestBody Map<String, Integer> body) {
        int userId = body.get("userId");
        int teamId = body.get("teamId");
        manager.recordAttendance(userId, teamId);
        return ResponseEntity.ok(Map.of("message", "Attendance recorded", "points", 5));
    }

    /**
     * Called by dashboard to show ScoreForTheTeam section.
     * Returns total + breakdown for last 30 days.
     *
     * GET /api/v1/score/team/1
     */
    @GetMapping("/team/{teamId}")
    public ResponseEntity<TeamScore> getTeamScore(@PathVariable int teamId) {
        return ResponseEntity.ok(manager.getTeamScore(teamId));
    }
}