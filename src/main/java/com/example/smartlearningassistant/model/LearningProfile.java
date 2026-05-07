package com.example.smartlearningassistant.model;

import java.util.ArrayList;
import java.util.List;

public class LearningProfile {
    private Long userId;
    private String goal;
    private String level;
    private List<String> interests = new ArrayList<>();
    private List<String> weakPoints = new ArrayList<>();
    private int dailyTargetMinutes;

    public LearningProfile() {
    }

    public LearningProfile(Long userId, String goal, String level, List<String> interests, List<String> weakPoints, int dailyTargetMinutes) {
        this.userId = userId;
        this.goal = goal;
        this.level = level;
        this.interests = interests;
        this.weakPoints = weakPoints;
        this.dailyTargetMinutes = dailyTargetMinutes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public List<String> getWeakPoints() {
        return weakPoints;
    }

    public void setWeakPoints(List<String> weakPoints) {
        this.weakPoints = weakPoints;
    }

    public int getDailyTargetMinutes() {
        return dailyTargetMinutes;
    }

    public void setDailyTargetMinutes(int dailyTargetMinutes) {
        this.dailyTargetMinutes = dailyTargetMinutes;
    }
}
