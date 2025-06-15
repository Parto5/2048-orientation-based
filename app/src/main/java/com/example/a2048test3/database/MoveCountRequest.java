package com.example.a2048test3.database;


public class MoveCountRequest {
    private String username;
    private int move_count;

    // Konstruktor, gettery, settery
    public MoveCountRequest(String username, int move_count) {
        this.username = username;
        this.move_count = move_count;
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

