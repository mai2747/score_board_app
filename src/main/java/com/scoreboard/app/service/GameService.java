package com.scoreboard.app.service;


import com.scoreboard.app.Exception.ValidationException;
import com.scoreboard.app.model.*;
import com.scoreboard.app.repository.GameRepository;
import com.scoreboard.app.util.DateTimeUtils;
import com.scoreboard.app.viewmodel.*;
import com.scoreboard.app.view.ViewManager;
import javafx.util.Pair;

import java.time.LocalDateTime;
import java.util.*;

public class GameService {
    private List<PlayerInGame> orderedPlayersInGame;
    private Map<Long, PlayerInGame> pigByPigId;
    private Map<Long, String> nameByPlayerId;

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
    private int consecutiveZeroCount = 0;
    private int playerNum;

    public GameService(ScoreService scoreService, GroupService groupService, GameRepository gameRepository){
        this.scoreService = scoreService;
        this.groupService = groupService;
        this.gameRepository = gameRepository;
        rankingService = new RankingService();
    }

    public void createNewGroup(List<String> names, String groupName, boolean isTemporary) {
        currentGroup = groupService.createGroup(names, isTemporary);

        System.out.println("Creating new group");

        if(!groupName.isBlank()) currentGroup.setGroupName(groupName);
        groupService.saveGroup(currentGroup);
    }

    public void selectGroup(Long groupID){
        currentGroup = groupService.getGroupById(groupID);
    }

    public void createAndStartGame(List<Long> orderedIDs, boolean enableTimer, int timerSeconds) {
        createAndInitialiseGameState(enableTimer, timerSeconds);
        registerPlayersWithOrder(orderedIDs);

        activateGroup();
    }

    public void prepareAndResumeGame(Long gameId){
        // set current Game/Group
        currentGame = gameRepository.findById(gameId).orElseThrow();
        currentGroup = groupService.getGroupById(currentGame.getGroupId());
        playerNum = currentGroup.getPlayers().size();
        nameByPlayerId = createPlayerNameMap(currentGame.getGameId(), currentGroup.getGroupId());

        // dummy
        if(currentGame.getGameRule().matches("DEFAULT")){
            currentGame.setSettings(createNewGameSettings(false, 0));
        }

        resumeGame();
        cancelPausedGame();

        // Set consecutiveZeroCount & currentTurnIndex & currentPlayer
        orderedPlayersInGame = groupService.findPlayersByGameId(gameId);
        pigByPigId = createPigByPigIdMap();
        previousScore = scoreService.findLatestByGameId(gameId).orElse(null); // need to be nullable

        if (previousScore != null) {
            currentTurnIndex = previousScore.getTurnNumber() + 1;

            previousPlayer = groupService.findPlayerByPigId(previousScore.getPlayerInGameId()).orElseThrow();

            int prevIndex = -1;
            Long previousPigId = previousScore.getPlayerInGameId();

            for (int i = 0; i < orderedPlayersInGame.size(); i++) {
                if (orderedPlayersInGame.get(i).getPigId().equals(previousPigId)) {
                    prevIndex = i;
                    break;
                }
            }

            if (prevIndex == -1) {
                throw new IllegalStateException("Previous player was not found in orderedPlayersInGame.");
            }

            int nextIndex = (prevIndex + 1) % playerNum;
            currentPlayer = orderedPlayersInGame.get(nextIndex);

            currentRanking = updateRanking(gameId);

        } else {
            // For paused game without any score input
            currentTurnIndex = 1;
            previousPlayer = null;
            currentPlayer = orderedPlayersInGame.get(0);
        }
    }

    public void createAndInitialiseGameState(boolean enableTimer, int timerSeconds){
        System.out.println();
        System.out.println("--Start Refreshing Data--");
        System.out.println();

        consecutiveZeroCount = 0;
        currentTurnIndex = 1;

        GameSettings gameSettings = createNewGameSettings(enableTimer, timerSeconds);

        currentGame = new Game(currentGroup.getGroupId(), gameSettings);
        gameRepository.save(currentGame);
    }

    private void registerPlayersWithOrder(List<Long> orderedIDs) {
        orderedPlayersInGame = groupService.registerPlayersInGame(currentGroup, currentGame.getGameId(), orderedIDs);
        pigByPigId = createPigByPigIdMap();
        playerNum = orderedIDs.size();
        currentPlayer = orderedPlayersInGame.get(0);
        nameByPlayerId = createPlayerNameMap(currentPlayer.getGameId(), currentGroup.getGroupId());

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

    private Map<Long, String> createPlayerNameMap(Long gameId, Long groupId) {
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
        List<Score> scores = getScores(currentGame.getGameId());

        if (useLastScoreAsPrevious && !scores.isEmpty()) {
            previousScore = scores.get(scores.size() - 1);
        }

        currentRanking = updateRanking(currentPlayer.getGameId());
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

        System.out.println("Next player is " + nameByPlayerId.get(currentPlayer.getPlayerId()));
        System.out.println();
    }

    public void printGameInfoLog(){
        if(!currentGroup.isTemporary()){
            // TODO: Check if the scores in Game entity and repository the same when ending a game
            currentGame.setScores(getScores(currentGame.getGameId()));

            Group group = groupService.getGroupById(currentGame.getGroupId());
            System.out.println("Saved this game");
            System.out.println();
            System.out.println("Group Name: " + group.getGroupName() + "\n" +
                    "GroupID: " + group.getGroupId() + "\n" +
                    "GameID: " + currentGame.getGameId() + "\n" +
                    "Player Num: " + group.getPlayers().size() + "\n" + "Players:");
            for(Player p: group.getPlayers()){
                System.out.println(p.getName() + " (id " + p.getId() +")");
            }
        }
    }

    public void handleTemporaryGroup(){
        if(currentGroup.isTemporary()){
            groupService.deleteGroup(currentGroup.getGroupId());
        }
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

    public void finishGame() {
        changeGameStatus(GameStatus.FINISHED);
        groupService.updateLastPlayedAt(currentGroup.getGroupId());
    }

    public void pauseGame() {
        changeGameStatus(GameStatus.PAUSED);
    }

    public void resumeGame() {
        changeGameStatus(GameStatus.IN_PROGRESS);
    }

    public void cancelGame() {
        changeGameStatus(GameStatus.CANCELLED);

        Long groupId = currentGame.getGroupId();
        long remaining = gameRepository.countByGroupId(groupId);
        if (remaining == 0) {
            draftGroup();
        }
    }

    private void changeGameStatus(GameStatus newStatus) {
        if (currentGame == null) {
            throw new IllegalStateException("Current game is null");
        }

        currentGame.setGameStatus(newStatus);
        gameRepository.updateStatus(currentGame.getGameId(), newStatus);
    }

    public void activateGroup() {
        changeGroupStatus(GroupStatus.ACTIVE);
    }

    public void draftGroup() {
        changeGroupStatus(GroupStatus.DRAFT);
    }

    private void changeGroupStatus(GroupStatus newStatus) {
        if (currentGame == null) {
            throw new IllegalStateException("Current game is null");
        }

        currentGroup.setStatus(newStatus);
        groupService.updateStatus(currentGroup.getGroupId(), newStatus);
    }

    public void deleteGamesByStatus(GameStatus status){
        gameRepository.deleteByStatus(status);
    }

    public void deleteGameByGameId(Long gameId){
        gameRepository.deleteByGameId(gameId);
    }

    public void cancelPausedGame(){
        gameRepository.updateStatusByCurrentStatus(GameStatus.PAUSED, GameStatus.CANCELLED);
    }

    private Map<Long, PlayerInGame> createPigByPigIdMap() {
        Map<Long, PlayerInGame> map = new HashMap<>();

        for (PlayerInGame pig : orderedPlayersInGame) {
            map.put(pig.getPigId(), pig);
        }
        return map;
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

    public List<Score> getCurrentScores(){
        return getScores(currentGame.getGameId());
    }

    public List<Score> getScores(Long gameId){
        return scoreService.getScores(gameId);
    }

    public String getPlayerNameByPigId(Long pigId){
        return nameByPlayerId.getOrDefault(pigId, "Unknown Player");
    }

    public Pair<String, Integer> createCurrentTurnInfo(){
        String currentPlayer = getPlayerNameByPigId(getCurrentPlayer().getPigId());
        int round = ((currentTurnIndex - 1) / playerNum) + 1;

        return new Pair<>(currentPlayer, round);
    }

    public String getPrevPlayerName() {
        PlayerInGame prev = getPrevPlayer();
        if (prev == null) return "";

        return getPlayerNameByPigId(prev.getPigId());
    }

    public boolean hasPausedGame() {
        return gameRepository.existsByStatus(GameStatus.PAUSED);
    }

    // Assume at most one paused game exists
    public Game getPausedGame() {
        List<Game> pausedGames = gameRepository.findAllByStatus(GameStatus.PAUSED);
        return pausedGames.isEmpty() ? null : pausedGames.get(0);
    }

    public String getPausedGameGroupName(){
        var pausedGame = getPausedGame();
        if (pausedGame == null) return null;

        Long groupId = pausedGame.getGroupId();
        return groupService.getGroupNameByGroupId(groupId);
    }

    public List<PlayerTotalScore> makePlayerTotalScores(Long gameId){
        return groupService.findPlayerTotalScoreByGameId(gameId);
    }

    public RankingDTO updateRanking(Long gameId){
        List<PlayerTotalScore> playerTotalScores = makePlayerTotalScores(gameId);
        return rankingService.buildRanking(gameId, playerTotalScores);
    }

    public String getGroupNameByGameId(Long gameId){
        Game game = gameRepository.findById(gameId)
                .orElseThrow();
        Long groupId = game.getGroupId();

        return groupService.getGroupNameByGroupId(groupId);
    }

    public Group getGroupByGameId(Long gameId){
        Game game = gameRepository.findById(gameId)
                .orElseThrow();
        Long groupId = game.getGroupId();

        return groupService.getGroupById(groupId);
    }

    public Map<Long, PlayerWinRateDTO> getPlayerWinRatesByGroupId(Long groupId){
        return groupService.findPlayerWinRatesByGroupId(groupId);
    }

    public PlayerWinRateDTO findBestWinRatePlayer(Map<Long, PlayerWinRateDTO> winRates){
        return winRates.values().stream()
                .max(Comparator
                        .comparingDouble(PlayerWinRateDTO::winRate)
                        .thenComparingInt(PlayerWinRateDTO::wins)
                        .thenComparing(PlayerWinRateDTO::playerName, String.CASE_INSENSITIVE_ORDER)
                )
                .orElse(null);
    }

    public List<Game> findRecentFinishedGamesByGroupId(Long groupId){
        return gameRepository.findRecentFinishedGamesByGroupId(groupId);
    }

    public Map<Long, List<PlayerGameStatDTO>> getPlayerStatsByGroupId(Long groupId) {
        List<Game> games = findRecentFinishedGamesByGroupId(groupId);

        Map<Long, List<PlayerGameStatDTO>> result = new LinkedHashMap<>();

        int gameIndex = 1;

        for (Game game : games) {
            RankingDTO ranking = updateRanking(game.getGameId());

            for (RankingEntryDTO entry : ranking.entries()) {

                PlayerGameStatDTO dto = new PlayerGameStatDTO(
                        entry.playerId(),
                        entry.playerName(),
                        gameIndex,
                        entry.totalScore(),
                        entry.rank()
                );
                result.computeIfAbsent(entry.playerId(), k -> new ArrayList<>()).add(dto);
            }
            gameIndex++;
        }
        return result;
    }

    // Return should be String/List<Player>/Group???
    public List<Player> getCurrentPlayers(){
        return new ArrayList<>(currentGroup.getPlayers());
    }

    public Group getGroup(Long groupId){
        return groupService.getGroupById(groupId);
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

    public Score getPrevScore(){
        return previousScore;
    }

    public PlayerInGame getPrevPlayer(){
        return previousPlayer;
    }

    public void setNameByPlayerId(Long gameId, Long groupId) { nameByPlayerId = createPlayerNameMap(gameId, groupId); }

}
