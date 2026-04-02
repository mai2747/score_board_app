package com.scoreboard.app.repository;

import com.scoreboard.app.model.Player;
import com.scoreboard.app.viewmodel.PlayerWinRateDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PlayerRepository {
    long reserveId();
    Player save(Player player);                 // insert or update
    void update(Player player);
    Optional<Player> findByPlayerId(Long playerId);
    List<Player> findByGroupId(Long groupId);
    Optional<Player> findByGroupIdAndName(Long groupId, String name);
    Map<Long, PlayerWinRateDTO> findPlayerWinRatesByGroupId(Long groupId);
}
