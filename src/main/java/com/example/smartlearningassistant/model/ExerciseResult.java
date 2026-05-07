package com.example.smartlearningassistant.model;

import java.time.LocalDateTime;

public class ExerciseResult {
    private Long exerciseId;
    private boolean correct;
    private String correctAnswer;
    private String explanation;
    private LocalDateTime submittedAt;

    public ExerciseResult(Long exerciseId, boolean correct, String correctAnswer, String explanation, LocalDateTime submittedAt) {
        this.exerciseId = exerciseId;
        this.correct = correct;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.submittedAt = submittedAt;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public boolean isCorrect() {
        return correct;
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
