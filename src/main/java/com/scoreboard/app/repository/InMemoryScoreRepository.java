package com.scoreboard.app.repository;

import com.scoreboard.app.model.Score;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryScoreRepository implements ScoreRepository{

    private final List<Score> scores = new ArrayList<>();

    @Override
    public void save(Score score) {
        scores.add(score);
        System.out.println("Score's saved to the Memory");
    }

    @Override
    public List<Score> findByGameId(Long gameId) {
        return scores.stream()
                .filter(s -> s.getGameId().equals(gameId))
                .collect(Collectors.toList());
    }

    // Get scores to make ranking
    @Override
    public List<Score> getScores(){
        return scores;
    }

}
