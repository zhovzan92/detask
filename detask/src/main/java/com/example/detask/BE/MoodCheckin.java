package com.example.detask.BE;

public class MoodCheckin {
    private int id;
    private int userId;
    private int teamId;
    private int mood; // 1–5
    private long timestamp;

    public MoodCheckin() {}

    public MoodCheckin(int userId, int teamId, int mood, long timestamp) {
        this.userId = userId;
        this.teamId = teamId;
        this.mood = mood;
        this.timestamp = timestamp;
    }

    private int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    private void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTeamId() {
        return teamId;
    }

    private void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getMood() {
        return mood;
    }

    private void setMood(int mood) {
        this.mood = mood;
    }

    public long getTimestamp() {
        return timestamp;
    }

    private void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
// getters and setters...
}