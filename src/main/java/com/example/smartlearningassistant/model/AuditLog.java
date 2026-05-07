package com.example.smartlearningassistant.model;

import java.time.LocalDateTime;

public class AuditLog {
    private Long id;
    private Long userId;
    private String action;
    private String detail;
    private LocalDateTime createdAt;

    public AuditLog(Long id, Long userId, String action, String detail, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.detail = detail;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAction() {
        return action;
    }

    public String getDetail() {
        return detail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
