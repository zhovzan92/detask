package com.example.detask.BLL;

import com.example.detask.BE.TeamScore;
import com.example.detask.DLL.TeamScoreDAO;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TeamScoreManager {

    private static final int SURVEY_POINTS  = 1; // per worker per daily completion
    private static final int ATTEND_POINTS  = 5; // per worker per attend press
    private static final int WINDOW_DAYS    = 30;

    private final TeamScoreDAO repo;

    public TeamScoreManager(TeamScoreDAO repo) {
        this.repo = repo;
    }

    /**
     * Called by Chrome extension when a worker completes all 3 daily surveys.
     * Awards 1 point — answer values are NOT stored or counted here.
     */
    public void recordSurveyCompletion(int userId, int teamId) {
        if (userId <= 0 || teamId <= 0) return;
        repo.saveSurveyCompletion(userId, teamId, System.currentTimeMillis());
    }

    /**
     * Called by Chrome extension when a worker presses "Attend".
     * Awards 5 points.
     */
    public void recordAttendance(int userId, int teamId) {
        if (userId <= 0 || teamId <= 0) return;
        repo.saveAttendance(userId, teamId, System.currentTimeMillis());
    }

    /**
     * Called by dashboard to get the team score for the last 30 days.
     * Formula:
     *   surveyPoints = count of survey completions × 1
     *   attendPoints = count of attendances × 5
     *   totalPoints  = surveyPoints + attendPoints
     *   averageScore = totalPoints / teamSize
     */
    public TeamScore getTeamScore(int teamId) {
        long since = Instant.now()
                .minus(WINDOW_DAYS, ChronoUnit.DAYS)
                .toEpochMilli();

        int completions = repo.countSurveyCompletions(teamId, since);
        int attendances = repo.countAttendances(teamId, since);
        int teamSize    = repo.countTeamMembers(teamId);

        int surveyPoints = completions * SURVEY_POINTS; // 1 each
        int attendPoints = attendances * ATTEND_POINTS;  // 5 each

        return new TeamScore(teamId, surveyPoints, attendPoints, teamSize);
    }
}