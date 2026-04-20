package com.scoreboard.app.service;

import com.scoreboard.app.model.GameStatus;
import com.scoreboard.app.repository.GameRepository;
import com.scoreboard.app.util.DateTimeUtils;

import java.time.LocalDateTime;

public class MaintenanceService {
    private GroupService groupService;
    private GameRepository gameRepository;

    public MaintenanceService(GroupService groupService, GameRepository gameRepository){
        this.groupService = groupService;
        this.gameRepository = gameRepository;
    }

    public void cleanDb(){
        pauseRemainingInProgressGames();
        deleteGamesByStatus(GameStatus.CANCELLED);

        LocalDateTime threshold = LocalDateTime.now().minusDays(7);

        String thresholdText = DateTimeUtils.format(threshold);

        groupService.deleteOldGroup(thresholdText);
    }

    // Replace IN_PROGRESS to PAUSED in case an app crashed and remaining IN_PROGRESS status
    public void pauseRemainingInProgressGames() {
        gameRepository.updateStatusByCurrentStatus(GameStatus.IN_PROGRESS, GameStatus.PAUSED);
    }

    public void cancelPausedGame(){
        gameRepository.updateStatusByCurrentStatus(GameStatus.PAUSED, GameStatus.CANCELLED);
    }

    public void deleteGameByGameId(Long gameId){
        gameRepository.deleteByGameId(gameId);
    }

    public void deleteGamesByStatus(GameStatus status){
        gameRepository.deleteByStatus(status);
    }
}
