package com.example.smartlearningassistant.model;

import java.time.LocalDateTime;

public class KnowledgeItem {
    private Long id;
    private String title;
    private String category;
    private String content;
    private LocalDateTime updatedAt;

    public KnowledgeItem() {
    }

    public KnowledgeItem(Long id, String title, String category, String content, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.content = content;
        this.updatedAt = updatedAt;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
