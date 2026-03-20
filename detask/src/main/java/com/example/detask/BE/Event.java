package com.example.detask.BE;

public class Event {

    private int id;
    private int teamId;
    private String name;
    private String description;
    private String startsAt;      // stored as ISO string for simplicity
    private String status;        // "DRAFT", "PUBLISHED", "CANCELLED"
    private int createdBy;        // admin/lead user ID

    public Event() {}

    public Event(int id, int teamId, String name, String description,
                 String startsAt, String status, int createdBy) {
        this.id = id;
        this.teamId = teamId;
        this.name = name;
        this.description = description;
        this.startsAt = startsAt;
        this.status = status;
        this.createdBy = createdBy;
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartsAt() { return startsAt; }
    public void setStartsAt(String startsAt) { this.startsAt = startsAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
}
