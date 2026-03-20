package com.example.detask.DLL;

import com.example.detask.BE.HealthScore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class HealthScoreDAO {

    private final JdbcTemplate jdbc;

    public HealthScoreDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<HealthScore> rowMapper = (rs, rowNum) -> {
        HealthScore h = new HealthScore();
        h.setId(rs.getInt("id"));
        h.setUserId(rs.getInt("userId"));
        h.setTeamId(rs.getInt("teamId"));
        h.setSurveyId(rs.getInt("surveyId"));
        h.setScore(rs.getInt("score"));
        h.setSubmittedAt(rs.getLong("submittedAt"));
        return h;
    };

    // Save a single survey submission
    public HealthScore save(HealthScore h) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO HealthScores (userId, teamId, surveyId, score, submittedAt) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, h.getUserId());
            ps.setInt(2, h.getTeamId());
            ps.setInt(3, h.getSurveyId());
            ps.setInt(4, h.getScore());
            ps.setLong(5, h.getSubmittedAt());
            return ps;
        }, keys);

        if (keys.getKey() != null) h.setId(keys.getKey().intValue());
        return h;
    }

    // All submissions for a team for a specific survey since a given time
    public List<HealthScore> findByTeamAndSurvey(int teamId, int surveyId, long sinceMs) {
        return jdbc.query(
                "SELECT id, userId, teamId, surveyId, score, submittedAt " +
                        "FROM HealthScores " +
                        "WHERE teamId = ? AND surveyId = ? AND submittedAt >= ? " +
                        "ORDER BY submittedAt ASC",
                rowMapper, teamId, surveyId, sinceMs
        );
    }

    // All submissions for a team (all surveys) since a given time
    public List<HealthScore> findByTeam(int teamId, long sinceMs) {
        return jdbc.query(
                "SELECT id, userId, teamId, surveyId, score, submittedAt " +
                        "FROM HealthScores " +
                        "WHERE teamId = ? AND submittedAt >= ? " +
                        "ORDER BY surveyId, submittedAt ASC",
                rowMapper, teamId, sinceMs
        );
    }
}