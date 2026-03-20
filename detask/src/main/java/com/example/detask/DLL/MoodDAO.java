package com.example.detask.DLL;


import com.example.detask.BE.MoodCheckin;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MoodDAO {

    private final JdbcTemplate jdbc;

    public MoodDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }


    private final RowMapper<MoodCheckin> rowMapper = (rs, rowNum) ->
            new MoodCheckin(
                    rs.getInt("userId"),
                    rs.getInt("teamId"),
                    rs.getInt("mood"),
                    rs.getLong("timestamp")
            );

    /** Insert a single mood check-in. */
    public void save(MoodCheckin m) {
        jdbc.update(
                // Qualify schema if needed: dbo.MoodCheckins
                "INSERT INTO MoodCheckins (userId, teamId, mood, [timestamp]) VALUES (?, ?, ?, ?)",
                m.getUserId(), m.getTeamId(), m.getMood(), m.getTimestamp()
        );
    }

    /**
     * Return all check-ins for a team since (inclusive) a given epoch ms.
     * Use this for "today" by passing start-of-day in ms.
     */
    public List<MoodCheckin> findSinceForTeam(int teamId, long sinceInclusiveMs) {
        String sql =
                "SELECT userId, teamId, mood, [timestamp] " +
                        "FROM MoodCheckins " +
                        "WHERE teamId = ? AND [timestamp] >= ? " +
                        "ORDER BY [timestamp] ASC";

        return jdbc.query(sql, rowMapper, teamId, sinceInclusiveMs);
    }

    /**
     * (Optional) If you want the same behavior as EventDAO when no date is given
     * — return "upcoming only" — you can create an overload that chooses between
     * CURRENT_TIMESTAMP and a since-window. Example:
     */
    public List<MoodCheckin> findByTeamDefaultWindow(int teamId, Long sinceInclusiveMs) {
        // If sinceInclusiveMs is null -> default to "today"
        if (sinceInclusiveMs == null) {
            long startOfToday = java.time.LocalDate.now()
                    .atStartOfDay(java.time.ZoneId.systemDefault())
                    .toInstant().toEpochMilli();
            sinceInclusiveMs = startOfToday;
        }
        return findSinceForTeam(teamId, sinceInclusiveMs);
    }
}