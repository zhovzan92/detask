package com.example.detask.BE;

public class TeamScore {

    private int teamId;
    private double averageScore;     // average score per team member over 30 days
    private int surveyPoints;        // total survey completion points (1 per worker per day)
    private int attendPoints;        // total attend points (5 per attend)
    private int totalPoints;         // surveyPoints + attendPoints
    private int teamSize;            // number of members in the team

    public TeamScore(int teamId, int surveyPoints, int attendPoints, int teamSize) {
        this.teamId = teamId;
        this.surveyPoints = surveyPoints;
        this.attendPoints = attendPoints;
        this.totalPoints = surveyPoints + attendPoints;
        this.teamSize = teamSize;
        // Average per member — avoid division by zero
        this.averageScore = teamSize > 0 ? (double) totalPoints / teamSize : 0;
    }

    public int getTeamId()            { return teamId; }
    public double getAverageScore()   { return averageScore; }
    public int getSurveyPoints()      { return surveyPoints; }
    public int getAttendPoints()      { return attendPoints; }
    public int getTotalPoints()       { return totalPoints; }
    public int getTeamSize()          { return teamSize; }
}