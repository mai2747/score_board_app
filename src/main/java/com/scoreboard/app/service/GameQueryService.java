package com.scoreboard.app.service;

import com.scoreboard.app.model.*;
import com.scoreboard.app.repository.GameRepository;
import com.scoreboard.app.viewmodel.PlayerTotalScore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameQueryService {
    private GroupService groupService;
    private GameRepository gameRepository;

    public GameQueryService(GroupService groupService, GameRepository gameRepository){
        this.groupService = groupService;
        this.gameRepository = gameRepository;
    }

    public void updateStatus(Long gameId, GameStatus newStatus){
        gameRepository.updateStatus(gameId, newStatus);
    }

    public List<Game> findRecentFinishedGamesByGroupId(Long groupId){
        return gameRepository.findRecentFinishedGamesByGroupId(groupId);
    }

    public List<PlayerTotalScore> makePlayerTotalScores(Long gameId){
        return groupService.findPlayerTotalScoreByGameId(gameId);
    }

    public Map<Long, String> createPlayerNameMap(Long gameId, Long groupId) {
        Map<Long, String> nameByPigId = new HashMap<>();
        Map<Long, String> nameByID = new HashMap<>();

        System.out.println("Creating Player-Name Map");

        List<PlayerInGame> pigs = groupService.findPlayersByGameId(gameId);
        Group group = groupService.getGroupById(groupId);
        for (Player player : group.getPlayers()) {
            nameByID.put(player.getId(), player.getName());
            System.out.println("ID -> " + player.getId() + " / Name -> " + player);
        }

        for (PlayerInGame pig : pigs) {
            Long pigId = pig.getPigId();
            Long playerId = pig.getPlayerId();

            String name = nameByID.get(playerId);

            nameByPigId.put(pigId, name);

            System.out.println(
                    "pigId -> " + pigId +
                            " / playerId -> " + playerId +
                            " / name -> " + name
            );
        }
        return nameByPigId;
    }

    public String getPausedGameGroupName(){
        var pausedGame = getPausedGame();

        if (pausedGame == null) return null;

        Long groupId = pausedGame.getGroupId();
        return groupService.getGroupNameByGroupId(groupId);
    }

    public long getGameNumByGroupId(long groupId){
        return gameRepository.getGameNumByGroupId(groupId);
    }

    // TODO: prepare repository method to pick one
    public Game getPausedGame() {
        List<Game> pausedGames = gameRepository.findAllByStatus(GameStatus.PAUSED);
        return pausedGames.isEmpty() ? null : pausedGames.get(0);
    }

    public String getGroupNameByGameId(Long gameId){
        Game game = gameRepository.findById(gameId)
                .orElseThrow();
        Long groupId = game.getGroupId();

        return groupService.getGroupNameByGroupId(groupId);
    }
}
