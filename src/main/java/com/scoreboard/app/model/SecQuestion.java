package com.scoreboard.app.model;

public class SecQuestion {
    private final int id;
    private final String text;

    public SecQuestion(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() { return id; }
    public String getText() { return text; }

    @Override
    public String toString() { return text; }
}
