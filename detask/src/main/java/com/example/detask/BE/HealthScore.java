package com.example.detask.BE;


public class HealthScore {

    private int id;
    private int userId;       // who submitted
    private int teamId;       // which team
    private int surveyId;     // 1, 2, or 3
    private int score;        // total score for 5 questions (each 1–5), so range 5–25
    private long submittedAt; // epoch ms

    public HealthScore() {}

    public HealthScore(int userId, int teamId, int surveyId, int score, long submittedAt) {
        this.userId = userId;
        this.teamId = teamId;
        this.surveyId = surveyId;
        this.score = score;
        this.submittedAt = submittedAt;
    }

    public int getId()            { return id; }
    public void setId(int id)     { this.id = id; }

    public int getUserId()              { return userId; }
    public void setUserId(int userId)   { this.userId = userId; }

    public int getTeamId()              { return teamId; }
    public void setTeamId(int teamId)   { this.teamId = teamId; }

    public int getSurveyId()                { return surveyId; }
    public void setSurveyId(int surveyId)   { this.surveyId = surveyId; }

    public int getScore()             { return score; }
    public void setScore(int score)   { this.score = score; }

    public long getSubmittedAt()                  { return submittedAt; }
    public void setSubmittedAt(long submittedAt)  { this.submittedAt = submittedAt; }
}