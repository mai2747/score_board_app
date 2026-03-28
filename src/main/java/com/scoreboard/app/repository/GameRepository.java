package com.scoreboard.app.repository;

import com.scoreboard.app.model.Game;
import com.scoreboard.app.model.GameStatus;

import java.util.List;
import java.util.Optional;

public interface GameRepository {
    Game save(Game game);
    void updateStatus(Long gameId, GameStatus newStatus);
    Optional<Game> findById(Long id);
    List<Game> findAll();
}
