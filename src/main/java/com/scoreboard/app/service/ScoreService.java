package com.scoreboard.app.service;

import com.scoreboard.app.model.Score;
import com.scoreboard.app.repository.ScoreRepository;
import com.scoreboard.app.viewmodel.PlayerTotalScore;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class ScoreService {
    private final ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository repo){
        this.scoreRepository = repo;
    }

   public void saveScore(Score score){
        scoreRepository.save(score);
    }

    public void editPrevScore(Score score, int newScore){
        score.setScore(newScore);
        scoreRepository.update(score);
    }

    public List<PlayerTotalScore> makePlayerTotalScores(Long gameId){
        return scoreRepository.findPlayerTotalScoresByGameId(gameId);
    }

    public Optional<Score> findLatestByGameId(Long gameId){
        return scoreRepository.findLatestByGameId(gameId);
    }

    public List<Score> getScores(Long gameId){
        return scoreRepository.findScoresByGameId(gameId);
    }

    public void clearGameScores(Long gameId){
        scoreRepository.clearGameScores(gameId);
    }

    private Long generateDummyID(){
        return ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
    }
}