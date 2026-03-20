package com.example.detask.DLL;

import com.example.detask.BE.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class UserDAO {

    private final JdbcTemplate jdbc;

    public UserDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<User> rowMapper = (rs, rowNum) -> {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        int team = rs.getInt("teamId");
        u.setTeamId(rs.wasNull() ? 0 : team);
        return u;
    };

    public User getByUsername(String username) {
        List<User> results = jdbc.query(
                "SELECT id, username, password, role, teamId FROM Users WHERE LOWER(username) = LOWER(?)",
                rowMapper, username
        );
        return results.isEmpty() ? null : results.get(0);
    }

    public User getById(int id) {
        List<User> results = jdbc.query(
                "SELECT id, username, password, role, teamId FROM Users WHERE id = ?",
                rowMapper, id
        );
        return results.isEmpty() ? null : results.get(0);
    }

    // NEW
    public List<User> getAllUsers() {
        return jdbc.query(
                "SELECT id, username, password, role, teamId FROM Users ORDER BY id ASC",
                rowMapper
        );
    }

    public User insert(User u) {
        KeyHolder keys = new GeneratedKeyHolder();
        try {
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO Users (username, password, role, teamId) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, u.getUsername());
                ps.setString(2, u.getPassword());
                ps.setString(3, u.getRole());
                if (u.getTeamId() == 0) ps.setNull(4, java.sql.Types.INTEGER);
                else ps.setInt(4, u.getTeamId());
                return ps;
            }, keys);
        } catch (DuplicateKeyException dup) {
            return null;
        }

        if (keys.getKey() != null) u.setId(keys.getKey().intValue());
        return u;
    }
}