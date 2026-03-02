package com.scoreboard.app.service;


import com.scoreboard.app.Exception.ValidationException;
import com.scoreboard.app.dto.PlayerDTO;
import com.scoreboard.app.dto.RankingDTO;
import com.scoreboard.app.model.Game;
import com.scoreboard.app.model.Group;
import com.scoreboard.app.model.PlayerInGame;
import com.scoreboard.app.model.Score;
import com.scoreboard.app.view.ViewManager;

import javax.swing.text.View;
import java.util.*;

public class GameService {
    private List<PlayerInGame> playersInGame;      // Player list containing turn order
    private List<PlayerInGame> playersInTurnOrder; // Ordered player list
    private List<PlayerDTO> playerDTO;             // いらないかもPlayerDTO, containing playerID, name, turn order
    private Map<Long, String> nameByPlayerID;

    private Game currentGame;
    private Long currentGameID;
    private Group currentGroup;
    private PlayerInGame currentPlayer;
    private RankingDTO currentRanking;

    public GroupService groupService;
    public ScoreService scoreService;
    public RankingService rankingService;

    public int currentTurnIndex = 1;
    private int playerNum;
    private int consecutiveZeroCount = 0;

    public GameService(ScoreService scoreService, GroupService groupService){
        this.scoreService = scoreService;
        this.groupService = groupService;
        rankingService = new RankingService();
    }

    public void startGameWithNewGroup(List<String> names){
        createNewGroup(names); // Game Object does not have any use yet
        createNewGame();
        // Set gameID to the field
        currentGameID = getCurrentGame().getId();
    }

    public void createNewGroup(List<String> names) {
        // Order of player names correspond to the play order
        playerNum = names.size();
        currentGroup = groupService.createGroup(names);

        // Create playerInGame, containing the play order in this game
        playersInGame = groupService.makePlayerList(currentGroup);

        // Get player list in a playing order
        playersInTurnOrder = groupService.getOrderedPlayers(playersInGame);
        currentPlayer = playersInTurnOrder.get(0);

        // Create nameByID list. Treated as a cache when the system want to get player names from playerID.
        nameByPlayerID = makeNameList(names);

        // GameRepository / GamePlayerRepository に保存するのは後で
        // currentGame = gameRepository.save(currentGame)
        // gamePlayerRepository.batchInsert(playersInGame)
    }

    // Separated from the method above, but does not have proper meaning of use yet
    public void createNewGame(){
        // ** Using dummy information **
        // Create Game object  * Should "new" in this class be deleted?
        currentGame = new Game();
        currentGame.setGroupId(currentGroup.getId());
        currentGame.setId(0L);
    }

    private Map<Long, String> makeNameList(List<String> names) {
        Map<Long, String> nameByID = new HashMap<>();
        int len = names.size();

        // Since names is supposed to be obtained in the order the same as turn order,
        // playerID is assumed to correspond to the name order.
        // ** If this is considered to be vulnerable to the system, should be modified
        for(int i = 0; i < len; i++) {
            nameByID.put(playersInTurnOrder.get(i).getPlayerId(), names.get(i));
        }
        return nameByID;
    }

    public void submitScore(String scoreInField) throws ValidationException {
        Long playerID = currentPlayer.getPlayerId();
        int input = parseAndValidate(scoreInField);
        System.out.println("Score submitted: " + input);

        if(!isConsecutiveZero(input)) {
            scoreService.addScore(currentGameID, playerID, currentTurnIndex, input);
            // Update ranking
            List<Score> scores = scoreService.getScores();
            currentRanking = rankingService.buildRanking(currentGameID, scores, nameByPlayerID);

            advanceTurn();
        }else{
            System.out.println("---Game ends due to consecutive zeros---");
            // TODO: Display phrase to help players recognition,
            //       ex) "Scores zero for three round, Game end"
            ViewManager.switchTo("Result.fxml");
        }
    }

    private boolean isConsecutiveZero(int score) {
        int threshold = playerNum * 3;

        if (score == 0) {
            consecutiveZeroCount++;
            System.out.println("Score 0 was submitted (" + consecutiveZeroCount + "/" + threshold + ")");
            return consecutiveZeroCount >= threshold;
        }

        // non-zero breaks consecutive zeros
        if (consecutiveZeroCount > 0) {
            System.out.println("Zero streak reset");
            consecutiveZeroCount = 0;
        }
        return false;
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

        int value = Integer.parseInt(normalised);
        if (value < 0) {
            throw new ValidationException("Score must be 0 or greater.");
        }

        if(!normalised.matches("\\d+")){
            throw new ValidationException("Score must be integer");
        }

        return value;
    }

    public void advanceTurn() {
        currentPlayer = playersInTurnOrder.get(currentTurnIndex % playerNum);
        System.out.println("Next player is " + nameByPlayerID.get(currentPlayer.getPlayerId()));
        currentTurnIndex++;
        System.out.println();
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

    public String getCurrentPlayerName(){
        Long id = currentPlayer.getPlayerId();
        return nameByPlayerID.getOrDefault(id, "");
    }

    public RankingDTO getCurrentRanking(){
        return currentRanking;
    }

    public void reorder(){
        // For the additional function: Reorder & Start new game
    }
}
