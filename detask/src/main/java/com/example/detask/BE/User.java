package com.example.detask.BE;
import com.fasterxml.jackson.annotation.JsonIgnore;
public class User {

    private int id;
    private String username;
    private String password;   // store hashed later if you want
    private String role;       // "ADMIN", "WORKER", "LEAD"
    private int teamId;        // workers belong to a team; admin can have -1 or null

    public User() {}

    public User(int id, String username, String password, String role, int teamId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.teamId = teamId;
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    @JsonIgnore
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }
}

