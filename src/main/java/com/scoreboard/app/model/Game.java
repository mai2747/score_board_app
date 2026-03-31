package com.scoreboard.app.model;

import com.scoreboard.app.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private Long gameID;
    private Long groupId; // Replace with groupID?
    private LocalDateTime startedAt;
    private GameStatus gameStatus;
    private String gameRule;
    private List<Score> scores;

    // Consider later: Should combine them into the one entity class?
    private GameSettings gameSettings;

    public Game(Long groupId, GameSettings gameSettings) {
        this.groupId = groupId;
        this.gameSettings = gameSettings;
        startedAt = LocalDateTime.now();
        this.scores = new ArrayList<>();

        gameStatus = GameStatus.IN_PROGRESS;
        gameRule = "DEFAULT";  // dummy;
    }

    public Game(long gameId, long groupId, LocalDateTime startedAt, GameStatus staus, String gameRule){
        this.gameID = gameId;
        this.groupId = groupId;
        this.startedAt = startedAt;
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

    public String getStartedTime(){
        return DateTimeUtils.format(startedAt);
    }

    public List<Score> getScores(){ return scores; }
    public void setScores(List<Score> scores){ this.scores = scores; }

    public GameStatus getGameStatus(){ return gameStatus; }
    public void setGameStatus(GameStatus status) { gameStatus = status; }

    public String getGameRule(){ return gameRule; }
    public void setGameRule(String gameRule) {this.gameRule = gameRule; }
}
