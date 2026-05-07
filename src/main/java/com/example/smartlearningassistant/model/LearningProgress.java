package com.example.smartlearningassistant.model;

public class LearningProgress {
    private long submittedCount;
    private long correctCount;
    private long mistakeCount;
    private int accuracy;
    private int averagePlanProgress;
    private String suggestion;

    public LearningProgress(long submittedCount, long correctCount, long mistakeCount, int accuracy, int averagePlanProgress, String suggestion) {
        this.submittedCount = submittedCount;
        this.correctCount = correctCount;
        this.mistakeCount = mistakeCount;
        this.accuracy = accuracy;
        this.averagePlanProgress = averagePlanProgress;
        this.suggestion = suggestion;
    }

    public long getSubmittedCount() {
        return submittedCount;
    }

    public long getCorrectCount() {
        return correctCount;
    }

    public long getMistakeCount() {
        return mistakeCount;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public int getAveragePlanProgress() {
        return averagePlanProgress;
    }

    public String getSuggestion() {
        return suggestion;
    }
}
