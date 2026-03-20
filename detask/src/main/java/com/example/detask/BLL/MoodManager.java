package com.example.detask.BLL;


import com.example.detask.BE.MoodCheckin;
import com.example.detask.BE.TeamMoodAnalytics;
import com.example.detask.DLL.MoodDAO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class MoodManager {

    private final MoodDAO repo;

    public MoodManager(MoodDAO repo) {
        this.repo = repo;
    }

    /** Records a single mood entry from a worker. */
    public void recordMood(int userId, int teamId, int mood) {
        // Basic validation to avoid garbage in DB:
        if (userId <= 0 || teamId <= 0) return;
        if (mood < 1 || mood > 5) return;

        MoodCheckin c = new MoodCheckin(userId, teamId, mood, System.currentTimeMillis());
        repo.save(c);
    }

    /** Computes today's team analytics with k-anonymity (k=5) and % below 65%. */
    public TeamMoodAnalytics calculateTeamMood(int teamId) {
        if (teamId <= 0) return new TeamMoodAnalytics(0, 0, 0);

        long startOfToday = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        List<MoodCheckin> list = repo.findSinceForTeam(teamId, startOfToday);
        if (list.isEmpty()) return new TeamMoodAnalytics(0, 0, 0);

        int total = list.size();

        // Average mood on 1..5 scale → percentage 20..100
        double avgRaw = list.stream().mapToInt(MoodCheckin::getMood).average().orElse(0.0);
        int avgPercent = round((avgRaw / 5.0) * 100.0);

        long countBelow = list.stream()
                .filter(c -> ((c.getMood() / 5.0) * 100.0) < 65.0) // 1..3 are below 65%
                .count();
        int belowPercent = round((countBelow * 100.0) / total);

        // k-anonymity: hide the “below 65%” metric for very small groups
        if (total < 5) {
            return new TeamMoodAnalytics(avgPercent, -1, total);
        }
        return new TeamMoodAnalytics(avgPercent, belowPercent, total);
    }

    private int round(double x) { return (int) Math.round(x); }
}