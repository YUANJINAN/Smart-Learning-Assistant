package com.example.smartlearningassistant.model;

import java.time.LocalDateTime;

public class AgentConfig {
    private Long id;
    private String name;
    private String responsibility;
    private String modelName;
    private double temperature;
    private String status;
    private LocalDateTime updatedAt;

    public AgentConfig() {
    }

    public AgentConfig(Long id, String name, String responsibility, String modelName, double temperature, String status, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.responsibility = responsibility;
        this.modelName = modelName;
        this.temperature = temperature;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
