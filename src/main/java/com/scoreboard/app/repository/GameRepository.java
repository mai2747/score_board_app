package com.scoreboard.app.repository;

import com.scoreboard.app.model.Game;
import com.scoreboard.app.model.GameStatus;

import java.util.List;
import java.util.Optional;

public interface GameRepository {
    Game save(Game game);
    void updateStatus(Long gameId, GameStatus newStatus);
    long countByGroupId(Long groupId);
    void deleteByGameId(Long gameId);
    void deleteByStatus(GameStatus status);
    Optional<Game> findById(Long gameId);
    List<Game> findAll();
    List<Game> findAllByStatus(GameStatus status);

    boolean existsByStatus(GameStatus status);
    void updateStatusByCurrentStatus(GameStatus from, GameStatus to);
}
