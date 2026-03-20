package com.example.detask.BE;

public class TeamHealthAnalytics {

    private int teamId;
    private int surveyId;           // 1, 2, or 3
    private int averageScorePercent; // 0–100 (score normalized from 5–25 range)
    private int sampleSize;          // how many submissions (-1 if anonymized)

    public TeamHealthAnalytics(int teamId, int surveyId, int averageScorePercent, int sampleSize) {
        this.teamId = teamId;
        this.surveyId = surveyId;
        this.averageScorePercent = averageScorePercent;
        this.sampleSize = sampleSize;
    }

    public int getTeamId()                  { return teamId; }
    public int getSurveyId()                { return surveyId; }
    public int getAverageScorePercent()     { return averageScorePercent; }
    public int getSampleSize()              { return sampleSize; }
}