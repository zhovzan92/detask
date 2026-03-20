package com.example.detask.controller;

import com.example.detask.BE.Team;
import com.example.detask.BLL.TeamManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamManager teamManager;

    public TeamController(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    // GET /api/v1/teams
    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(teamManager.getAllTeams());
    }

    // GET /api/v1/teams/1
    @GetMapping("/{id}")
    public ResponseEntity<?> getTeam(@PathVariable int id) {
        Team team = teamManager.getById(id);
        if (team == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "NOT_FOUND", "message", "Team not found"));
        }
        return ResponseEntity.ok(team);
    }
}