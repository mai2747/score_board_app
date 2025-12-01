package com.scoreboard.app.service;


import com.scoreboard.app.model.Game;
import com.scoreboard.app.model.Group;
import com.scoreboard.app.model.PlayerInGame;

import java.util.ArrayList;
import java.util.List;

public class GameService {
    private List<PlayerInGame> playersInGame;
    private Game currentGame;
    private Group currentGroup;
    public GroupService groupService;

    public void createNewGroup(List<String> names) {
        groupService = new GroupService();
        currentGroup = groupService.createGroup(names);

        // ** Using dummy information **
        // Create Game object
        currentGame = new Game();
        currentGame.setGroupId(currentGroup.getId());
        currentGame.setId(0L);

        // Create playerInGame, containing the play order in this game
        playersInGame = new ArrayList<>();

        int order = 1;
        for (Long playerId : currentGroup.getPlayerIds()) {  // ← group内のプレイヤーID一覧
            PlayerInGame pig = new PlayerInGame(0l, playerId, order++);
            playersInGame.add(pig);
        }

        // GameRepository / GamePlayerRepository に保存するのは後で
        // currentGame = gameRepository.save(currentGame)
        // gamePlayerRepository.batchInsert(playersInGame)
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public List<PlayerInGame> getPlayersInGame() {
        return playersInGame;
    }
}
