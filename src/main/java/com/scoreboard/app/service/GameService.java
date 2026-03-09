package com.scoreboard.app.service;


import com.scoreboard.app.Exception.ValidationException;
import com.scoreboard.app.model.*;
import com.scoreboard.app.repository.GameRepository;
import com.scoreboard.app.viewmodel.RankingDTO;
import com.scoreboard.app.view.ViewManager;

import java.util.*;

public class GameService {
    private List<PlayerInGame> playersInGame;      // Player list containing turn order
    private List<PlayerInGame> playersInTurnOrder; // Ordered player list
    private Map<Long, String> nameByPlayerID;

    private Game currentGame;
    private Long currentGameID;
    private Group currentGroup;
    private PlayerInGame currentPlayer;
    private RankingDTO currentRanking;
    private PlayerInGame previousPlayer;
    private Score previousScore;

    public GroupService groupService;
    public ScoreService scoreService;
    public RankingService rankingService;
    public GameRepository gameRepository;

    public int currentTurnIndex = 1;
    private int playerNum;
    private int consecutiveZeroCount = 0;

    public GameService(ScoreService scoreService, GroupService groupService, GameRepository gameRepository){
        this.scoreService = scoreService;
        this.groupService = groupService;
        this.gameRepository = gameRepository;
        rankingService = new RankingService();
    }

    public void startGameWithNewGroup(List<String> names, boolean isTemporary){
        // ここを分岐させてnew Group/ exist Groupを分ける？
        // Set Group -> new / previous / stored Group
        currentGroup = groupService.createGroup(names, isTemporary);
        putGroupInfo(names, isTemporary); // Game Object does not have any use yet

        startGame();
    }

    public void startGame(){
        System.out.println();
        System.out.println("--Start Refreshing Data--");
        System.out.println();

        scoreService.clearScores();

        createNewGame();
        currentGameID = currentGame.getGameID();
    }

    public void putGroupInfo(List<String> names, boolean isTemporary) {
        // Order of player names correspond to the play order
        playerNum = names.size();

        // Create playerInGame, containing the play order in this game
        playersInGame = groupService.makePlayerList(currentGroup);

        // Get player list in a playing order
        playersInTurnOrder = groupService.getOrderedPlayers(playersInGame);
        currentPlayer = playersInTurnOrder.get(0);

        // Create nameByID list. Treated as a cache when the system want to get player names from playerID.
        nameByPlayerID = makeNameList(names);

        System.out.println("Current Player -> " + playersInTurnOrder.get(0).toString());
        for (PlayerInGame pig : playersInTurnOrder) {
            System.out.println("PIG: playerId=" + pig.getPlayerId() + ", order=" + pig.getTurnOrder());
        }

        // GameRepository / GamePlayerRepository に保存するのは後で
        // currentGame = gameRepository.save(currentGame)
        // gamePlayerRepository.batchInsert(playersInGame)
    }

    // Separated from the method above, but does not have proper meaning of use yet
    public void createNewGame(){
        // Delete this method if there's nothing more to add
        currentGame = new Game(currentGroup);
        currentGame.setGameID(gameRepository.reserveId());
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
        int input = parseAndValidate(scoreInField);
        Long playerId = currentPlayer.getPlayerId();

        System.out.println("Score submitted: " + input);

        if (isConsecutiveZero(input)) {
            endGame();
            return;
        }

        scoreService.addScore(currentGameID, playerId, currentTurnIndex, input);
        afterScoreChanged(true);

        advanceTurn();
    }

    public void editPrevScore(String scoreInField) throws ValidationException {
        int input = parseAndValidate(scoreInField);

        System.out.println("Previous score is edited: " + previousScore.getScore() + " -> " + input);

        if (isConsecutiveZero(input)) {
            endGame();
            return;
        }

        System.out.println("PrevScore ID: " + previousScore.getScoreId());
        scoreService.editPrevScore(previousScore.getScoreId(), input);
        afterScoreChanged(false);
    }

    private void afterScoreChanged(boolean useLastScoreAsPrevious) {
        List<Score> scores = scoreService.getScores();

        if (useLastScoreAsPrevious && !scores.isEmpty()) {
            previousScore = scores.get(scores.size() - 1);
        }

        currentRanking = rankingService.buildRanking(currentGameID, scores, nameByPlayerID);
    }

    private void endGame() {
        System.out.println("---Game ends due to consecutive zeros---");
        // TODO: Display phrase to help players recognition,
        //       ex) "Scores zero for three round, Game end"
        ViewManager.switchTo("Result.fxml");
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

    public void advanceTurn() {
        previousPlayer = currentPlayer;
        currentPlayer = playersInTurnOrder.get(currentTurnIndex++ % playerNum);

        System.out.println("Next player is " + nameByPlayerID.get(currentPlayer.getPlayerId()));
        System.out.println();
    }

    public void saveGame(){
        if(!currentGroup.isTemporary()){
            currentGame.setScores(scoreService.getScores());
            gameRepository.save(currentGame);

            Group group = currentGame.getGroup();
            System.out.println("Saved this game");
            System.out.println();
            System.out.println("GameID: " + currentGame.getGameID() + "\n" +
                    "GroupID: " + group.getGroupID() + "\n" +
                    "Group Name: " + group.getName() + "\n" +
                    "Player Num: " + group.getPlayers().size() + "\n" + "Players:");
            for(Player p: group.getPlayers()){
                System.out.println(p.getName() + " ID:" + p.getId());
            }

            System.out.println("Stored data number -> " + gameRepository.getStoredDataNum());
        }
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

    public Game getCurrentGame() {
        return currentGame;
    }

    public List<PlayerInGame> getPlayersInGame() {
        return playersInGame;
    }

    public PlayerInGame getCurrentPlayer() {
        return currentPlayer;
    }

    public String getPlayerName(PlayerInGame player){
        Long id = player.getPlayerId();
        return nameByPlayerID.getOrDefault(id, "");
    }

    public RankingDTO getCurrentRanking(){
        return currentRanking;
    }

    public Score getPrevScore(){
        return previousScore;
    }

    public PlayerInGame getPrevPlayer(){
        return previousPlayer;
    }

    public void reorder(){
        // For the additional function: Reorder & Start new game
    }
}
