package com.scoreboard.app.model;

public class Score {
    private Long scoreId;
    private Long playerInGameId;
    private int turnNumber;   // 1手目、2手目…
    private int score;       // このターンで獲得した点



    public Score(Long scoreId, Long playerInGameId, int turnNumber, int score){
        this.scoreId = scoreId;
        this.playerInGameId = playerInGameId;
        this.turnNumber = turnNumber;
        this.score = score;
    }

    public void setScore(int score){ this.score = score; }

    public void setScoreId(Long scoreId){ this.scoreId = scoreId; }

    public Long getScoreId(){ return scoreId; }

    public Long getPlayerInGameId() {
        return playerInGameId;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public int getScore() {
        return score;
    }
}
