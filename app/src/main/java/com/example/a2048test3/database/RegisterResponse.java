package com.example.a2048test3.database;

public class RegisterResponse {
    private String message;
    private String username;

    // Konstruktor, gettery i settery
    public RegisterResponse(String message, String username) {
        this.message = message;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
