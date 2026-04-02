package com.scoreboard.app.viewmodel;

public class ScoreRow {
    private final String playerName;
    private final int turn;
    private final int score;
    private final int total;

    public ScoreRow(String playerName, int turn, int score, int total) {
        this.playerName = playerName;
        this.turn = turn;
        this.score = score;
        this.total = total;
    }

    public String getPlayerName() { return playerName; }
    public int getTurn() { return turn; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
}
