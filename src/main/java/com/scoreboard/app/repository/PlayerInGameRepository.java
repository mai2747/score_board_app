package com.scoreboard.app.repository;

import com.scoreboard.app.model.PlayerInGame;
import com.scoreboard.app.viewmodel.PlayerTotalScore;

import java.util.List;
import java.util.Optional;

public interface PlayerInGameRepository {
    PlayerInGame save(PlayerInGame pig);
    Optional<PlayerInGame> findById(Long playerInGameId);
    List<PlayerInGame> findPlayersByGameId(Long gameId);
    List<PlayerTotalScore> findPlayerTotalScoreByGameId(Long gameId);
}
