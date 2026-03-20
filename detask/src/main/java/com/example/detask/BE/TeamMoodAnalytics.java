package com.example.detask.BE;


public class TeamMoodAnalytics {
    private int averageMoodPercent; // 0–100
    private int percentBelow65;     // -1 if anonymized
    private int sampleSize;

    public TeamMoodAnalytics(int avg, int below, int size) {
        this.averageMoodPercent = avg;
        this.percentBelow65 = below;
        this.sampleSize = size;
    }

    public int getAverageMoodPercent() { return averageMoodPercent; }
    public int getPercentBelow65() { return percentBelow65; }
    public int getSampleSize() { return sampleSize; }
}