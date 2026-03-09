package com.scoreboard.app.model;

public class Score {
    private Long scoreId;
    private Long gameId;
    private Long playerId;
    private int turnNumber;   // 1手目、2手目…
    private int score;       // このターンで獲得した点



    public Score(Long gameId, Long playerId, int turnNumber, int score){
        this.gameId = gameId;
        this.playerId = playerId;
        this.turnNumber = turnNumber;
        this.score = score;
    }

    public void setScore(int score){ this.score = score; }

    public void setScoreId(Long scoreId){ this.scoreId = scoreId; }

    public Long getScoreId(){ return scoreId; }

    public Long getGameId(){
        return gameId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public int getScore() {
        return score;
    }
}
