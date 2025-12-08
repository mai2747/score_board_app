package com.scoreboard.app.service;


import com.scoreboard.app.model.Game;
import com.scoreboard.app.model.Group;
import com.scoreboard.app.model.PlayerInGame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameService {
    private List<PlayerInGame> playersInGame;
    private List<PlayerInGame> playersInTurnOrder;
    private Game currentGame;
    private Long currentGameID;
    private Group currentGroup;
    private PlayerInGame currentPlayer;
    public GroupService groupService;
    public ScoreService scoreService;

    public int currentTurnIndex = 1;
    private int playerNum;

    public GameService(ScoreService scoreService){
        this.scoreService = scoreService;
    }

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

        // Get player list in a playing order
        playersInTurnOrder = getOrderedPlayers();
        playerNum = playersInGame.size();
        currentPlayer = playersInTurnOrder.get(0);

        // Set gameID to the field
        currentGameID = getCurrentGame().getId();

        // GameRepository / GamePlayerRepository に保存するのは後で
        // currentGame = gameRepository.save(currentGame)
        // gamePlayerRepository.batchInsert(playersInGame)
    }

    private List<PlayerInGame> getOrderedPlayers(){
        return playersInGame.stream().sorted(Comparator.comparingInt(PlayerInGame::getTurnOrder)).toList();
    }

    public void submitScore(Long playerId, int score){
        scoreService.addScore(currentGameID, playerId, currentTurnIndex, score);

        // Set for the next turn ... should be separated to another method??
        advanceTurn();
        currentPlayer = playersInTurnOrder.get(currentTurnIndex % playerNum);
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public List<PlayerInGame> getPlayersInGame() {
        return playersInGame;
    }

    public PlayerInGame getCurrentPlayer() {
        return playersInTurnOrder.get(currentTurnIndex % playerNum);
    }

    public void advanceTurn() {
        currentTurnIndex++;
    }
}
