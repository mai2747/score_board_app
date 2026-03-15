package com.scoreboard.app.model;

import com.scoreboard.app.service.GameService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
    private Long gameID;
    private Group group; // Replace with groupID?
    private LocalDateTime startedAt;
    private List<Score> scores;

    // Consider later: Should combine them into the one entity class?
    private GameSettings gameSettings;
    private List<PlayerInGame> playersInGame; // プレイ順を含む

    public Game(Group group, GameSettings gameSettings) {
        this.group = group;
        this.gameSettings = gameSettings;
        startedAt = LocalDateTime.now();
    }

    public GameSettings getSettings() {
        return gameSettings;
    }
    public void setSettings(GameSettings gameSettings) { this.gameSettings = gameSettings; }

    public Long getGameID() { return gameID; }
    public void setGameID(Long id) { this.gameID = id; } // repositoryからのみ

    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }

    public List<Score> getScores(){ return scores; }
    public void setScores(List<Score> scores){ this.scores = scores; }
}
