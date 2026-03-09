package com.scoreboard.app.repository.memory;

import com.scoreboard.app.model.Score;
import com.scoreboard.app.repository.ScoreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryScoreRepository implements ScoreRepository {

    private final AtomicLong seq = new AtomicLong(1);  // Threads safe
    private final List<Score> scores = new ArrayList<>();

    @Override
    public void save(Score score) {
        score.setScoreId(seq.getAndIncrement());
        scores.add(score);
        System.out.println("Score's saved to the Memory");
    }

    @Override
    public List<Score> findByGameId(Long gameId) {
        return scores.stream()
                .filter(s -> s.getGameId().equals(gameId))
                .collect(Collectors.toList());
    }

    @Override
    public Score findByScoreId(Long scoreId) {
        return scores.stream()
                .filter(s -> s.getScoreId().equals(scoreId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Score not found: " + scoreId));
    }

    @Override
    public void update(Long id, int newScore){
        scores.stream()
                .filter(s -> s.getScoreId().equals(id))
                .findFirst()
                .ifPresent(s -> s.setScore(newScore));
        System.out.println("Score's updated in the Memory");
    }

    // Get scores to make ranking
    @Override
    public List<Score> getScores(){
        return scores;
    }

    @Override
    public void clearScores(){
        scores.clear();
    }

}
