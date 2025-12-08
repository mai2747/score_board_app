package com.scoreboard.app.service;

import com.scoreboard.app.model.Score;
import com.scoreboard.app.repository.ScoreRepository;

public class ScoreService {
    private final ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository repo){
        this.scoreRepository = repo;
    }

    public void addScore(Long gameId, Long playerId, int turnNumber, int point){
        // Get id info and put them to the object below
        // Not sure where to store the list of scores yet, ScoreService(here) or GameService

        // For dummy ID
        Long dummyID = generateDummyID();

        // While creating Score object, since scoreID will be generated on the way to be saved to DB,
        // scoreID might be omitted from Score object in the actual installation
        // But still need to think about the chance of editing past score
        Score score = new Score(dummyID, gameId, playerId, turnNumber, point);

        scoreRepository.save(score);
    }

    private Long generateDummyID(){
        return 00l;
    }
}

//private Long id;
//private Long gameId;
//private Long playerId;
//private int turnNumber;   // 1手目、2手目…
//private int points;       // このターンで獲得した点
