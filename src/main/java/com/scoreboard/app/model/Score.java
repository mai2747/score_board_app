package com.scoreboard.app.model;

public class Score {
    private Long scoreId;
    private Long gameId;
    private Long playerId;
    private int turnNumber;   // 1手目、2手目…
    private int point;       // このターンで獲得した点



    public Score(Long scoreId, Long gameId, Long playerId, int turnNumber, int point){
        this.scoreId = scoreId;
        this.gameId = gameId;
        this.playerId = playerId;
        this.turnNumber = turnNumber;
        this.point = point;
    }

    public Long getGameId(){
        return gameId;
    }
}
