package com.scoreboard.app.model;

import java.time.LocalDateTime;
import java.util.List;

public class Game {
    private Long gameID;
    private Long groupId;
    private LocalDateTime startedAt;
    private List<Score> scores;

    private List<PlayerInGame> playersInGame; // プレイ順を含む

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setId(Long id) {
        this.gameID = id;
    }

    public Long getId() {
        return gameID;
    }
}
