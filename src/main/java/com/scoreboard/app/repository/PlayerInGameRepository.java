package com.scoreboard.app.repository;

import com.scoreboard.app.model.PlayerInGame;

import java.util.List;

public interface PlayerInGameRepository {
    PlayerInGame save(PlayerInGame pig);
    List<PlayerInGame> findPlayersByGameId(Long groupId);
}
