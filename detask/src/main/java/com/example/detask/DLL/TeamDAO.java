package com.example.detask.DLL;

import com.example.detask.BE.Team;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeamDAO {

    private final JdbcTemplate jdbc;

    public TeamDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Team> rowMapper = (rs, rowNum) -> {
        Team t = new Team();
        t.setId(rs.getInt("id"));
        t.setName(rs.getString("name"));
        return t;
    };

    public List<Team> getAllTeams() {
        return jdbc.query(
                "SELECT id, name FROM Teams ORDER BY id ASC",
                rowMapper
        );
    }

    public Team getById(int id) {
        List<Team> results = jdbc.query(
                "SELECT id, name FROM Teams WHERE id = ?",
                rowMapper, id
        );
        return results.isEmpty() ? null : results.get(0);
    }
}
