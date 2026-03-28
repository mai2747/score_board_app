package com.scoreboard.app.model;

import java.time.LocalDateTime;
import java.util.List;

public class Game {
    private Long gameID;
    private Long groupId; // Replace with groupID?
    private LocalDateTime startedAt;
    private List<Score> scores;
    private String gameStatus;
    private String gameRule;

    // Consider later: Should combine them into the one entity class?
    private GameSettings gameSettings;
    private List<PlayerInGame> playersInGame; // プレイ順を含む

    public Game(Long groupId, GameSettings gameSettings) {
        this.groupId = groupId;
        this.gameSettings = gameSettings;
        startedAt = LocalDateTime.now();

        gameStatus = "IN_PROGRESS";
        gameRule = "DEFAULT";  // dummy;
    }

    public Game(long gameId, long groupId, String staus, String gameRule){
        this.gameID = gameId;
        this.groupId = groupId;
        this.gameStatus = staus;
        this.gameRule = gameRule;
    }

    public GameSettings getSettings() {
        return gameSettings;
    }
    public void setSettings(GameSettings gameSettings) { this.gameSettings = gameSettings; }

    public Long getGameId() { return gameID; }
    public void setGameID(Long id) { this.gameID = id; } // repositoryからのみ

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public List<Score> getScores(){ return scores; }
    public void setScores(List<Score> scores){ this.scores = scores; }

    public String getGameStatus(){ return gameStatus; }
    public void setGameStatus(String status) { gameStatus = status; }

    public String getGameRule(){ return gameRule; }
    public void setGameRule(String gameRule) {this.gameRule = gameRule; }
}
