package com.example.smartlearningassistant.model;

import java.util.ArrayList;
import java.util.List;

public class CourseUnit {
    private Long id;
    private String title;
    private String subject;
    private String difficulty;
    private String summary;
    private List<String> objectives = new ArrayList<>();

    public CourseUnit() {
    }

    public CourseUnit(Long id, String title, String subject, String difficulty, String summary, List<String> objectives) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.difficulty = difficulty;
        this.summary = summary;
        this.objectives = objectives;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<String> objectives) {
        this.objectives = objectives;
    }
}
