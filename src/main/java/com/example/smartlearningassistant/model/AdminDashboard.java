package com.example.smartlearningassistant.model;

import java.util.Map;

public class AdminDashboard {
    private long userCount;
    private long courseCount;
    private long exerciseCount;
    private long planCount;
    private long knowledgeCount;
    private Map<String, Long> agentStatus;

    public AdminDashboard(long userCount, long courseCount, long exerciseCount, long planCount, long knowledgeCount, Map<String, Long> agentStatus) {
        this.userCount = userCount;
        this.courseCount = courseCount;
        this.exerciseCount = exerciseCount;
        this.planCount = planCount;
        this.knowledgeCount = knowledgeCount;
        this.agentStatus = agentStatus;
    }

    public long getUserCount() {
        return userCount;
    }

    public long getCourseCount() {
        return courseCount;
    }

    public long getExerciseCount() {
        return exerciseCount;
    }

    public long getPlanCount() {
        return planCount;
    }

    public long getKnowledgeCount() {
        return knowledgeCount;
    }

    public Map<String, Long> getAgentStatus() {
        return agentStatus;
    }
}
