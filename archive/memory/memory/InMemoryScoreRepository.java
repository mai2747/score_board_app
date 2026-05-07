package com.scoreboard.app.repository.memory;

import com.scoreboard.app.model.Score;
import com.scoreboard.app.repository.ScoreRepository;
import com.scoreboard.app.viewmodel.PlayerTotalScore;

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
    public void update(Score score){
        scores.stream()
                .filter(s -> s.getScoreId().equals(score.getScoreId()))
                .findFirst()
                .ifPresent(s -> s.setScore(score.getScore()));
        System.out.println("Score's updated in the Memory");
    }

    @Override
    public List<Score> findScoresByGameId(Long gameId) {
        return scores.stream()
                .filter(s -> s.getGameId().equals(gameId)) // getGameId() is deleted while switching to DB
                .collect(Collectors.toList());
    }

    // For SQLite ver.
    @Override
    public List<PlayerTotalScore> findPlayerTotalScoresByGameId(Long gameId) {
        return List.of();
    }

    @Override
    public Score findByScoreId(Long scoreId) {
        return scores.stream()
                .filter(s -> s.getScoreId().equals(scoreId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Score not found: " + scoreId));
    }

    // Get scores to make ranking
    @Override
    public List<Score> getScores(){
        return scores;
    }

    @Override
    public void clearGameScores(Long gameId){
        scores.clear();
    }

}
