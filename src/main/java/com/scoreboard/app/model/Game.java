package com.scoreboard.app.model;

import java.time.LocalDateTime;
import java.util.List;

public class Game {
    private Long id;
    private Long groupId;
    private LocalDateTime startedAt;
    private List<Score> scores;

    private List<PlayerInGame> playersInGame; // プレイ順を含む

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
