package com.scoreboard.app.repository;

import com.scoreboard.app.model.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {
    long reserveId();
    Player save(Player player);                 // insert or update
    void update(Player player);
    Optional<Player> findByPlayerId(Long playerId);
    List<Player> findByGroupId(Long groupId);
    Optional<Player> findByGroupIdAndName(Long groupId, String name);
}
