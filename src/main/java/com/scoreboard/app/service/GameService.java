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

    public void startGame(){
        System.out.println();
        System.out.println("--Start Refreshing Data--");
        System.out.println();

        scoreService.clearScores();
        consecutiveZeroCount = 0;
        currentTurnIndex = 1;

        createNewGame();
        currentGameID = currentGame.getGameID();
    }

    public void startGameWithNewGroup(List<String> names, boolean isTemporary) {
        currentGroup = groupService.createGroup(names, isTemporary);

        List<Long> orderedIDs = currentGroup.getPlayers().stream()
                .map(Player::getId)
                .toList();

        initializeGameWithOrderedIDs(orderedIDs);
        startGame();
    }

    public void startGameWithExistingGroup(List<Long> orderedIDs) {
        initializeGameWithOrderedIDs(orderedIDs);
        startGame();
    }

    private void initializeGameWithOrderedIDs(List<Long> orderedIDs) {
        playerNum = orderedIDs.size();

        playersInGame = groupService.makePlayerList(currentGroup, orderedIDs);
        playersInTurnOrder = groupService.getOrderedPlayers(playersInGame);
        currentPlayer = playersInTurnOrder.get(0);
        nameByPlayerID = makeNameList();

        System.out.println("Current Player -> " + currentPlayer);
        for (PlayerInGame pig : playersInTurnOrder) {
            System.out.println("PIG: playerId=" + pig.getPlayerId() + ", order=" + pig.getTurnOrder());
        }
    }

    public void selectGroup(Long groupID){
        currentGroup = groupService.getGroupByID(groupID);
    }

    // Separated from the method above, but does not have proper meaning of use yet
    public void createNewGame(){
        // Delete this method if there's nothing more to add
        currentGame = new Game(currentGroup);
        currentGame.setGameID(gameRepository.reserveId());
    }

    private Map<Long, String> makeNameList() {
        Map<Long, String> nameByID = new HashMap<>();

        for (Player player : currentGroup.getPlayers()) {
            nameByID.put(player.getId(), player.getName());
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
                    "Group Name: " + group.getGroupName() + "\n" +
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

    public String getPlayerName(PlayerInGame player){
        Long id = player.getPlayerId();
        return nameByPlayerID.getOrDefault(id, "");
    }

    // Return should be String/List<Player>/Group???
    public List<Player> getPlayers(){
        List<Player> players = new ArrayList<>();

        players.addAll(currentGroup.getPlayers());
        return players;
    }

    public PlayerInGame getCurrentPlayer() {
        return currentPlayer;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public RankingDTO getCurrentRanking(){
        return currentRanking;
    }

    public List<PlayerInGame> getPlayersInGame() {
        return playersInGame;
    }

    public Group getCurrentGroup(){
        return currentGroup;
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
