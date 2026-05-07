package com.example.smartlearningassistant.model;

public class LoginResponse {
    private Long userId;
    private String username;
    private String displayName;
    private UserRole role;
    private String status;

    public LoginResponse(UserAccount account) {
        this.userId = account.getId();
        this.username = account.getUsername();
        this.displayName = account.getDisplayName();
        this.role = account.getRole();
        this.status = account.getStatus();
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UserRole getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }
}
