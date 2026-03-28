package com.scoreboard.app.service;


import com.scoreboard.app.Exception.ValidationException;
import com.scoreboard.app.model.*;
import com.scoreboard.app.repository.GameRepository;
import com.scoreboard.app.viewmodel.PlayerTotalScore;
import com.scoreboard.app.viewmodel.RankingDTO;
import com.scoreboard.app.view.ViewManager;
import com.scoreboard.app.viewmodel.RankingEntryDTO;

import java.util.*;

public class GameService {
    private List<PlayerInGame> orderedPlayersInGame;
    private Map<Long, String> nameByPlayerID;

    private Game currentGame;
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

    public void createNewGroup(List<String> names, String groupName, boolean isTemporary) {
        currentGroup = groupService.createGroup(names, isTemporary);
        if(!groupName.isBlank()) currentGroup.setGroupName(groupName);
        groupService.saveGroup(currentGroup);
    }

    public void createAndStartGame(List<Long> orderedIDs, boolean enableTimer, int timerSeconds) {
        createAndInitialiseGameState(enableTimer, timerSeconds);
        registerPlayersWithOrder(orderedIDs);

    }

    public void createAndInitialiseGameState(boolean enableTimer, int timerSeconds){
        System.out.println();
        System.out.println("--Start Refreshing Data--");
        System.out.println();

        consecutiveZeroCount = 0;
        currentTurnIndex = 1;

        GameSettings gameSettings = createNewGameSettings(enableTimer, timerSeconds);

        currentGame = new Game(currentGroup.getGroupID(), gameSettings);
        gameRepository.save(currentGame);
    }

    private void registerPlayersWithOrder(List<Long> orderedIDs) {
        orderedPlayersInGame = groupService.registerPlayersInGame(currentGroup, currentGame.getGameId(), orderedIDs);
        playerNum = orderedIDs.size();
        currentPlayer = orderedPlayersInGame.get(0);
        nameByPlayerID = createPlayerNameMap();

        for (PlayerInGame pig : orderedPlayersInGame) {
            System.out.println("PIG: playerId=" + pig.getPlayerId() + ", order=" + pig.getTurnOrder());
        }
    }

    public GameSettings createNewGameSettings(boolean enableTimer, int timerSeconds){
        TimerSettings timerSettings;

        if(timerSeconds > 0){
            timerSettings = TimerSettings.ofSeconds(timerSeconds);
        }else{
            timerSettings = TimerSettings.off();
        }

        return new GameSettings(enableTimer, timerSettings);
    }

    public void updateGameSettings(GameSettings newGameSettings){
        currentGame.setSettings(newGameSettings);
    }

    private Map<Long, String> createPlayerNameMap() {
        Map<Long, String> nameByID = new HashMap<>();

        for (Player player : currentGroup.getPlayers()) {
            nameByID.put(player.getId(), player.getName());
        }

        return nameByID;
    }

    public void selectGroup(Long groupID){
        currentGroup = groupService.getGroupById(groupID);
    }

    public void submitScore(String scoreInField) throws ValidationException {
        int input = parseAndValidate(scoreInField);

        System.out.println("Score submitted: " + input);

        if (isConsecutiveZero(input)) {
            endGame();
            return;
        }

        Score score = new Score(null, currentPlayer.getPigId(), currentTurnIndex, input);
        scoreService.saveScore(score);
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
        scoreService.editPrevScore(previousScore, input);
        afterScoreChanged(false);
    }

    private void afterScoreChanged(boolean useLastScoreAsPrevious) {
        List<Score> scores = scoreService.getScores(currentGame.getGameId());

        if (useLastScoreAsPrevious && !scores.isEmpty()) {
            previousScore = scores.get(scores.size() - 1);
        }

        List<PlayerTotalScore> playerTotalScores = scoreService.makePlayerTotalScores(currentGame.getGameId());
        currentRanking = rankingService.buildRanking(currentGame.getGameId(), playerTotalScores);
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
        currentPlayer = orderedPlayersInGame.get(currentTurnIndex++ % playerNum);

        System.out.println("Next player is " + nameByPlayerID.get(currentPlayer.getPlayerId()));
        System.out.println();
    }

    public void printGameInfoLog(){
        if(!currentGroup.isTemporary()){
            // TODO: Check if the scores in Game entity and repository the same when ending a game
            currentGame.setScores(scoreService.getScores(currentGame.getGameId()));

            Long groupId = currentGame.getGroupId();
            Group group = groupService.getGroupById(groupId);
            System.out.println("Saved this game");
            System.out.println();
            System.out.println("GameID: " + currentGame.getGameId() + "\n" +
                    "GroupID: " + group.getGroupID() + "\n" +
                    "Group Name: " + group.getGroupName() + "\n" +
                    "Player Num: " + group.getPlayers().size() + "\n" + "Players:");
            for(Player p: group.getPlayers()){
                System.out.println(p.getName() + " ID:" + p.getId());
            }
        }
    }

    public List<Score> getScores(){
        return scoreService.getScores(currentGame.getGameId());
    }

    public void startGame() {
        changeGameStatus(GameStatus.IN_PROGRESS);
    }

    public void finishGame() {
        changeGameStatus(GameStatus.FINISHED);
    }

    public void pauseGame() {
        changeGameStatus(GameStatus.PAUSED);
    }

    public void resumeGame() {
        changeGameStatus(GameStatus.IN_PROGRESS);
    }

    public void cancelGame() {
        changeGameStatus(GameStatus.CANCELLED);
    }

    private void changeGameStatus(GameStatus newStatus) {
        if (currentGame == null) {
            throw new IllegalStateException("Current game is null");
        }

        currentGame.setGameStatus(newStatus);
        gameRepository.updateStatus(currentGame.getGameId(), newStatus);
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

        if (!trimmed.matches("[0-9]+")) {
            throw new ValidationException("Score must be a half-width integer.");
        }

        int value = Integer.parseInt(trimmed);
        if (value < 0) {
            throw new ValidationException("Score must be 0 or greater.");
        }

        return value;
    }

    public String getPlayerNameByID(Long id){
        return nameByPlayerID.getOrDefault(id, "Unknown Player");
    }

    // Return should be String/List<Player>/Group???
    public List<Player> getPlayers(){
        List<Player> players = new ArrayList<>();

        players.addAll(currentGroup.getPlayers());
        return players;
    }

    private void ensureGameStarted() {
        if (currentGame == null) {
            throw new IllegalStateException("Game has not started.");
        }
    }

    public PlayerInGame getCurrentPlayer() {
        return currentPlayer;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public List<RankingEntryDTO> getCurrentRanking(){
        return currentRanking.entries();
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

}
