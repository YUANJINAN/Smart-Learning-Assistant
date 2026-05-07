package com.example.smartlearningassistant.model;

import java.util.ArrayList;
import java.util.List;

public class AgentReply {
    private String tutorAdvice;
    private String plannerAdvice;
    private String reviewAdvice;
    private List<String> recommendedResources = new ArrayList<>();

    public AgentReply(String tutorAdvice, String plannerAdvice, String reviewAdvice, List<String> recommendedResources) {
        this.tutorAdvice = tutorAdvice;
        this.plannerAdvice = plannerAdvice;
        this.reviewAdvice = reviewAdvice;
        this.recommendedResources = recommendedResources;
    }

    public String getTutorAdvice() {
        return tutorAdvice;
    }

    public String getPlannerAdvice() {
        return plannerAdvice;
    }

    public String getReviewAdvice() {
        return reviewAdvice;
    }

    public List<String> getRecommendedResources() {
        return recommendedResources;
    }
}
