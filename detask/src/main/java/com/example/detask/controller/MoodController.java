package com.example.detask.controller;


import com.example.detask.BE.TeamMoodAnalytics;
import com.example.detask.BLL.MoodManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/mood")
public class MoodController {

    private final MoodManager manager;

    public MoodController(MoodManager manager) {
        this.manager = manager;
    }

    // POST /api/v1/mood
    // Body: { "userId": 1, "teamId": 10, "mood": 3 }
    @PostMapping
    public ResponseEntity<?> submitMood(@RequestBody Map<String, Integer> body) {
        int userId = body.get("userId");
        int teamId = body.get("teamId");
        int mood   = body.get("mood");

        if (mood < 1 || mood > 5) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "mood must be between 1 and 5"));
        }

        manager.recordMood(userId, teamId, mood);
        return ResponseEntity.ok(Map.of("message", "Mood saved"));
    }

    // GET /api/v1/mood/team/10
    @GetMapping("/team/{teamId}")
    public ResponseEntity<TeamMoodAnalytics> getTeamMood(@PathVariable int teamId) {
        return ResponseEntity.ok(manager.calculateTeamMood(teamId));
    }
}
