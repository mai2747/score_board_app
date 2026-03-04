package com.scoreboard.app.service;

import com.scoreboard.app.model.Score;
import com.scoreboard.app.repository.ScoreRepository;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ScoreService {
    private final ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository repo){
        this.scoreRepository = repo;
    }

    public void addScore(Long gameId, Long playerId, int turnNumber, int point){
        // scoreID can be omitted from Score object in the actual installation when switching to DB
        // But still need to think about the chance of editing past score
        Score score = new Score(gameId, playerId, turnNumber, point);

        scoreRepository.save(score);
    }

    public List<Score> getScores(){
        return scoreRepository.getScores();
    }

    public void clearScores(){
        scoreRepository.clearScores();
    }

    private Long generateDummyID(){
        return ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
    }
}

//private Long id;
//private Long gameId;
//private Long playerId;
//private int turnNumber;   // 1手目、2手目…
//private int points;       // このターンで獲得した点
