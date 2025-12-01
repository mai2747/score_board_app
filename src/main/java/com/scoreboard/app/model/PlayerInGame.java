package com.scoreboard.app.model;

public class PlayerInGame {
    private Long gameId;
    private Long playerId;
    private int turnOrder;   // 1番手, 2番手...

    // Set the turnOrder as the same as given name's order at the first time
    // if user continued to the next game, then edit turnOrder to reorder.
    public PlayerInGame(Long gameId, Long playerId, int turnOrder){
        this.gameId = gameId;
        this.playerId = playerId;
        this.turnOrder = turnOrder;
    }
}
