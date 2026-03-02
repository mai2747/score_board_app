package com.scoreboard.app.repository;

import com.scoreboard.app.model.Player;
import java.util.Optional;

public interface PlayerRepository {
    Player save(Player player);                 // insert or update
    Optional<Player> findById(Long id);
}
