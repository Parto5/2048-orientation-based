package com.example.a2048test3.database;

public class LoginResponse {
    private String message;
    private String username;
    private int move_count;

    // Konstruktor, gettery i settery

    public LoginResponse(String message, String username, int move_count) {
        this.message = message;
        this.username = username;
        this.move_count = move_count;
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

    public int getMove_count() {
        return move_count;
    }

    public void setMove_count(int move_count) {
        this.move_count = move_count;
    }
}
