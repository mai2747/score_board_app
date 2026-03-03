package com.scoreboard.app.repository;

import com.scoreboard.app.model.Score;
import java.util.List;

// Prepared an interface, considering the ease of switching between in-memory demos,
// production environments using databases, and the potential future use of APIs

public interface ScoreRepository {

    void save(Score score);               // INSERT
    List<Score> getScores();
    void clearScores();
    List<Score> findByGameId(Long gameId); // For getting scores of a game (not used in demo)
}