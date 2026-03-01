package com.scoreboard.app.service;


import com.scoreboard.app.Exception.ValidationException;
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

    public GameService(ScoreService scoreService, GroupService groupService){
        this.scoreService = scoreService;
        this.groupService = groupService;
    }

    public void createNewGroup(List<String> names) {
        playerNum = names.size();
        currentGroup = groupService.createGroup(names);

        // Create playerInGame, containing the play order in this game
        playersInGame = groupService.makePlayerList(currentGroup);

        // Get player list in a playing order
        playersInTurnOrder = groupService.getOrderedPlayers(playersInGame);
        currentPlayer = playersInTurnOrder.get(0);

        // Set gameID to the field
        currentGameID = getCurrentGame().getId();

        // GameRepository / GamePlayerRepository に保存するのは後で
        // currentGame = gameRepository.save(currentGame)
        // gamePlayerRepository.batchInsert(playersInGame)
    }

    // Separated from the method above, but does not have proper meaning of use yet
    public void createNewGame(){
        // ** Using dummy information **
        // Create Game object  * ok to be "new" in this class?
        currentGame = new Game();
        currentGame.setGroupId(currentGroup.getId());
        currentGame.setId(0L);
    }

    public void submitScore(String scoreInField) throws ValidationException {
        Long playerID = currentPlayer.getPlayerId();
        int score = parseAndValidate(scoreInField);
        scoreService.addScore(currentGameID, playerID, currentTurnIndex, score);

        // Set for the next turn ... should be separated to another method??
        advanceTurn();
        currentPlayer = playersInTurnOrder.get(currentTurnIndex % playerNum);
    }

    // Error handling: Score is null/negative/non-digit
    private int parseAndValidate(String input) throws ValidationException{
        if (input == null) {
            throw new ValidationException("Score is required.");
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException("Score is required.");
        }

        if (trimmed.contains(" ")) {
            throw new ValidationException("Spaces are not allowed.");
        }
        String normalised = trimmed;

        if(!normalised.matches("\\d+")){
            throw new ValidationException("Score must be integer");
        }

        int value = Integer.parseInt(normalised);
        if (value < 0) {
            throw new ValidationException("Score must be 0 or greater.");
        }

        return value;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public List<PlayerInGame> getPlayersInGame() {
        return playersInGame;
    }

    public PlayerInGame getCurrentPlayer() {
        return currentPlayer;
    }

    public void advanceTurn() {
        currentTurnIndex++;
    }
}
