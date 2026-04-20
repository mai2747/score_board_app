package com.scoreboard.app.service;


import com.scoreboard.app.Exception.ValidationException;
import com.scoreboard.app.GamePlayContext;
import com.scoreboard.app.model.*;
import com.scoreboard.app.repository.GameRepository;
import com.scoreboard.app.viewmodel.*;
import javafx.util.Pair;

import java.util.*;

public class GameService {
    private Group currentGroup;
    private GamePlayContext gamePlayContext;

    public GroupService groupService;
    public ScoreService scoreService;
    public GameRepository gameRepository;
    public RankingService rankingService;
    public MaintenanceService maintenanceService;
    public GameQueryService gameQueryService;
    public GamePlayService gamePlayService;

    public GameService(ScoreService scoreService, GroupService groupService, GameRepository gameRepository, MaintenanceService maintenanceService, GameQueryService gameQueryService, GamePlayService gamePlayService){
        this.scoreService = scoreService;
        this.groupService = groupService;
        this.gameRepository = gameRepository;
        this.maintenanceService = maintenanceService;
        this.gameQueryService = gameQueryService;
        this.gamePlayService = gamePlayService;
        rankingService = new RankingService();
    }

    public void createNewGroup(List<String> names, String groupName, boolean isTemporary) {
        currentGroup = groupService.createGroup(names, isTemporary);

        System.out.println("Creating new group");

        if(!groupName.isBlank()) currentGroup.setGroupName(groupName);
        groupService.saveGroup(currentGroup);
    }

    public void setCurrentGroupById(Long groupID){
        currentGroup = groupService.getGroupById(groupID);
    }

    public GamePlayContext createAndStartGame(List<Long> orderedIds, boolean enableTimer, int timerSeconds) {
        System.out.println("--Start Refreshing Data--");
        System.out.println();

        GameSettings gameSettings = createNewGameSettings(enableTimer, timerSeconds);
        Game game = new Game(currentGroup.getGroupId(), gameSettings);
        gameRepository.save(game);

        List<PlayerInGame> orderedPlayers = groupService.registerPlayersInGame(
                currentGroup, game.getGameId(), orderedIds);

        Map<Long, PlayerInGame> pigByPigId = createPigByPigIdMap(orderedPlayers);
        Map<Long, String> nameByPlayerId = createPlayerNameMapByPigId(game.getGameId(), currentGroup.getGroupId());

        for (PlayerInGame pig : orderedPlayers) {
            System.out.println("PIG: playerId=" + pig.getPlayerId() + ", order=" + pig.getTurnOrder());
        }

        activateGroup();

        return GamePlayContext.forNewGame(game, orderedPlayers, pigByPigId, nameByPlayerId);
    }

    public GamePlayContext prepareAndResumeGame(Long gameId){
        // set current Game/Group
        Game game = gameRepository.findById(gameId).orElseThrow();
        setCurrentGroupById(game.getGroupId());
        Map<Long, String> nameByPlayerId = createPlayerNameMapByPigId(game.getGameId(), currentGroup.getGroupId());

        // dummy
        if(game.getGameRule().matches("DEFAULT")){
            game.setSettings(createNewGameSettings(false, 0));
        }

        resumeGame();
        cancelPausedGame();

        //TODO: Set consecutiveZeroCount
        List<PlayerInGame> orderedPlayersInGame = groupService.findPlayersByGameId(gameId);
        Map<Long, PlayerInGame> pigByPigId = createPigByPigIdMap(orderedPlayersInGame);
        Score previousScore = scoreService.findLatestByGameId(gameId).orElse(null); // need to be nullable

        if (previousScore != null) {
            int currentTurnIndex = previousScore.getTurnNumber() + 1;

            PlayerInGame previousPlayer = groupService.findPlayerByPigId(previousScore.getPlayerInGameId()).orElseThrow();

            int nextIndex = getNextIndex(previousScore, orderedPlayersInGame);
            PlayerInGame currentPlayer = orderedPlayersInGame.get(nextIndex);

            RankingDTO currentRanking = calculateRanking(gameId);

            return GamePlayContext.forResumedGame(
                    game, orderedPlayersInGame, pigByPigId, nameByPlayerId,
                    currentPlayer, previousPlayer, previousScore, currentRanking,
                    currentTurnIndex, 0);  //TODO: update zero count (now default)

        } else {
            // For paused game without any score input
            PlayerInGame currentPlayer = orderedPlayersInGame.get(0);

            return GamePlayContext.forResumedGame(
                    game, orderedPlayersInGame, pigByPigId, nameByPlayerId,
                    currentPlayer, null, null, null,
                    1, 0);
        }
    }

    private int getNextIndex(Score previousScore, List<PlayerInGame> orderedPlayersInGame) {
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

        return (prevIndex + 1) % orderedPlayersInGame.size();
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
        gamePlayService.updateGameSettings(newGameSettings);
    }

    private Map<Long, String> createPlayerNameMapByPigId(Long gameId, Long groupId) {
        return gameQueryService.createPlayerNameMap(gameId, groupId);
    }

    public void submitScore(String scoreInField) throws ValidationException {
        gamePlayService.submitScore(scoreInField);
    }

    public void editPrevScore(String scoreInField) throws ValidationException {
        gamePlayService.editPrevScore(scoreInField);
    }

    // Replace IN_PROGRESS to PAUSED in case an app crashed and remaining IN_PROGRESS status
    public void pauseRemainingInProgressGames() {
        maintenanceService.pauseRemainingInProgressGames();
    }

    public void closeGame() { gamePlayService.finishGame(); }

    public void handleTemporaryGroup(){
        if(currentGroup.isTemporary()){
            groupService.deleteGroup(currentGroup.getGroupId());
        }
    }

    public void cleanDb(){ maintenanceService.cleanDb(); }

    public void pauseGame() {
        gamePlayService.pauseGame();
    }

    public void resumeGame() {
        gamePlayService.resumeGame();
    }

    public void cancelGame() { gamePlayService.cancelGame(); }


    public void activateGroup() {
        gamePlayService.activateGroup();
    }

    public void deleteGameByGameId(Long gameId){
        maintenanceService.deleteGameByGameId(gameId);
    }

    public void cancelPausedGame(){
        maintenanceService.cancelPausedGame();
    }

    // move to GameQueryService?
    private Map<Long, PlayerInGame> createPigByPigIdMap(List<PlayerInGame> orderedPlayersInGame) {
        Map<Long, PlayerInGame> map = new HashMap<>();

        for (PlayerInGame pig : orderedPlayersInGame) {
            map.put(pig.getPigId(), pig);
        }
        return map;
    }

    public RankingDTO calculateRanking(Long gameId){
        return  gamePlayService.calculateRanking(gameId);
    }

    public void setGamePlayContext(GamePlayContext context) {
        this.gamePlayContext = context;
        // GamePlayServiceにも同じコンテキストを渡す
        this.gamePlayService.setGamePlayContext(context);
    }

    public GamePlayContext getGamePlayContext() {
        return gamePlayContext;
    }

    public List<Score> getCurrentScores(){
        return gamePlayService.getCurrentScores();
    }

    public List<Score> getScores(Long gameId){
        return gamePlayService.getScores(gameId);
    }

    public String getPlayerNameByPigId(Long pigId){
        return gamePlayService.getPlayerNameByPigId(pigId);
    }

    public Pair<String, Integer> createCurrentTurnInfo(){
        return gamePlayService.createCurrentTurnInfo();
    }

    public String getPrevPlayerName() {
        return gamePlayService.getPrevPlayerName();
    }

    public boolean hasPausedGame() {
        return gameRepository.existsByStatus(GameStatus.PAUSED);
    }

    public Game getPausedGame() {
        return gameQueryService.getPausedGame();
    }

    public String getPausedGameGroupName(){
        return gameQueryService.getPausedGameGroupName();
    }

    public List<PlayerTotalScore> makePlayerTotalScores(Long gameId){
        return gamePlayService.makePlayerTotalScores(gameId);
    }

    public String getGroupNameByGameId(Long gameId){
        return gameQueryService.getGroupNameByGameId(gameId);
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
        return gameQueryService.findRecentFinishedGamesByGroupId(groupId);
    }

    public Map<Long, List<PlayerGameStatDTO>> getPlayerStatsByGroupId(Long groupId) {
        List<Game> games = findRecentFinishedGamesByGroupId(groupId);

        Map<Long, List<PlayerGameStatDTO>> result = new LinkedHashMap<>();

        int gameIndex = 1;

        for (Game game : games) {
            RankingDTO ranking = calculateRanking(game.getGameId());

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

    public List<Player> getPlayersByGroupId(Long groupId){
        return groupService.getPlayersByGroupId(groupId);
    }

    public Group getGroup(Long groupId){
        return groupService.getGroupById(groupId);
    }

    public Game getCurrentGame() {
        return gamePlayService.getCurrentGame();
    }

    public List<RankingEntryDTO> getCurrentRanking(){
        return gamePlayService.getCurrentRanking();
    }

    public Score getPrevScore(){
        return gamePlayService.getPrevScore();
    }

    //public void setNameByPlayerId(Long gameId, Long groupId) { nameByPlayerId = createPlayerNameMapByPigId(gameId, groupId); }

}
