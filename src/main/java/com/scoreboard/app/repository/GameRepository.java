package com.scoreboard.app.repository;

import com.scoreboard.app.model.Game;
import java.util.Optional;

public interface GameRepository {
    Game save(Game game);
    Optional<Game> findById(Long id);
}
