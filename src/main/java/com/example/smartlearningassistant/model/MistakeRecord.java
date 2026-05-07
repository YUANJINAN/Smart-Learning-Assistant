package com.example.smartlearningassistant.model;

import java.time.LocalDateTime;

public class MistakeRecord {
    private Long exerciseId;
    private String question;
    private String submittedAnswer;
    private String correctAnswer;
    private String explanation;
    private LocalDateTime submittedAt;

    public MistakeRecord(Long exerciseId, String question, String submittedAnswer, String correctAnswer, String explanation, LocalDateTime submittedAt) {
        this.exerciseId = exerciseId;
        this.question = question;
        this.submittedAnswer = submittedAnswer;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.submittedAt = submittedAt;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public String getQuestion() {
        return question;
    }

    public String getSubmittedAnswer() {
        return submittedAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
}
