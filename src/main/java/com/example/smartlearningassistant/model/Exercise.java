package com.example.smartlearningassistant.model;

import java.util.ArrayList;
import java.util.List;

public class Exercise {
    private Long id;
    private Long courseId;
    private String question;
    private List<String> options = new ArrayList<>();
    private String answer;
    private String explanation;

    public Exercise() {
    }

    public Exercise(Long id, Long courseId, String question, List<String> options, String answer, String explanation) {
        this.id = id;
        this.courseId = courseId;
        this.question = question;
        this.options = options;
        this.answer = answer;
        this.explanation = explanation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
