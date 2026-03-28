package com.scoreboard.app.repository;

import com.scoreboard.app.model.Score;
import com.scoreboard.app.viewmodel.PlayerTotalScore;

import java.util.List;

// Prepared an interface, considering the ease of switching between in-memory demos,
// production environments using databases, and the potential future use of APIs

public interface ScoreRepository {

    void save(Score score); // INSERT
    void update(Score score); // UPDATE
    List<Score> getScores();
    void clearGameScores(Long gameId);
    List<Score> findScoresByGameId(Long gameId); // For getting scores of a game (not used in demo)
    List<PlayerTotalScore> findPlayerTotalScoresByGameId(Long gameId);
    Score findByScoreId(Long ScoreId);
}