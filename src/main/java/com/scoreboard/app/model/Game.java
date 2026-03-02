package com.scoreboard.app.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
    private Long gameID;
    private Group group;
    private LocalDateTime startedAt;
    private List<Score> scores;

    private List<PlayerInGame> playersInGame; // プレイ順を含む

    public Game(Group group) {
        this.group = group;
        startedAt = LocalDateTime.now();
    }

    public Long getGameID() { return gameID; }
    public void setGameID(Long id) { this.gameID = id; } // repositoryからのみ

    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }
}
