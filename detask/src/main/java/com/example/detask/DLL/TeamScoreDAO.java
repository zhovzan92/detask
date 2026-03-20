package com.example.detask.DLL;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TeamScoreDAO {

    private final JdbcTemplate jdbc;

    public TeamScoreDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Save a survey completion (1 point)
    public void saveSurveyCompletion(int userId, int teamId, long completedAt) {
        jdbc.update(
                "INSERT INTO SurveyCompletions (userId, teamId, completedAt) VALUES (?, ?, ?)",
                userId, teamId, completedAt
        );
    }

    // Save an attendance (5 points)
    public void saveAttendance(int userId, int teamId, long attendedAt) {
        jdbc.update(
                "INSERT INTO Attendances (userId, teamId, attendedAt) VALUES (?, ?, ?)",
                userId, teamId, attendedAt
        );
    }

    // Count survey completions for a team in the last 30 days
    public int countSurveyCompletions(int teamId, long sinceMs) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM SurveyCompletions WHERE teamId = ? AND completedAt >= ?",
                Integer.class, teamId, sinceMs
        );
        return count != null ? count : 0;
    }

    // Count attendances for a team in the last 30 days
    public int countAttendances(int teamId, long sinceMs) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Attendances WHERE teamId = ? AND attendedAt >= ?",
                Integer.class, teamId, sinceMs
        );
        return count != null ? count : 0;
    }

    // Count how many distinct users are in the team (from Users table)
    public int countTeamMembers(int teamId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Users WHERE teamId = ?",
                Integer.class, teamId
        );
        return count != null ? count : 0;
    }
}