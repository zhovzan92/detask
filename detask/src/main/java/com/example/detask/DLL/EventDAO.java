package com.example.detask.DLL;

import com.example.detask.BE.Event;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventDAO {

    private final JdbcTemplate jdbc;

    public EventDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Event> rowMapper = (rs, rowNum) -> {
        Event e = new Event();
        e.setId(rs.getInt("id"));
        e.setTeamId(rs.getInt("teamId"));
        e.setName(rs.getString("name"));
        e.setDescription(rs.getString("description"));
        e.setStartsAt(rs.getString("startsAt"));
        e.setStatus(rs.getString("status"));
        e.setCreatedBy(rs.getInt("createdBy"));
        return e;
    };

    // NEW — all events
    public List<Event> getAllEvents() {
        return jdbc.query(
                "SELECT id, teamId, name, description, startsAt, status, createdBy " +
                        "FROM Events ORDER BY startsAt ASC",
                rowMapper
        );
    }

    // NEW — single event by id
    public Event getById(int id) {
        List<Event> results = jdbc.query(
                "SELECT id, teamId, name, description, startsAt, status, createdBy " +
                        "FROM Events WHERE id = ?",
                rowMapper, id
        );
        return results.isEmpty() ? null : results.get(0);
    }

    public Event save(Event e) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Events (teamId, name, description, startsAt, status, createdBy) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, e.getTeamId());
            ps.setString(2, e.getName());
            ps.setString(3, e.getDescription());
            ps.setString(4, e.getStartsAt());
            ps.setString(5, e.getStatus());
            ps.setInt(6, e.getCreatedBy());
            return ps;
        }, keys);

        if (keys.getKey() != null) e.setId(keys.getKey().intValue());
        return e;
    }

    public List<Event> findByTeamAndDateRange(int teamId, String from, String to) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, teamId, name, description, startsAt, status, createdBy " +
                        "FROM Events WHERE teamId = ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(teamId);

        boolean filterByDates = (from != null || to != null);
        if (!filterByDates) {
            sql.append("AND startsAt >= CURRENT_TIMESTAMP ");
        } else {
            if (from != null) { sql.append("AND CAST(startsAt AS DATE) >= CAST(? AS DATE) "); params.add(from); }
            if (to   != null) { sql.append("AND CAST(startsAt AS DATE) <= CAST(? AS DATE) "); params.add(to); }
        }
        sql.append("ORDER BY startsAt ASC");

        return jdbc.query(sql.toString(), rowMapper, params.toArray());
    }
}