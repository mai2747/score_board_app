package com.scoreboard.app.model;

public class SecurityQuestion {
    private final int id;
    private final String text;

    public SecurityQuestion(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() { return id; }
    public String getText() { return text; }

    @Override
    public String toString() { return text; }
}
