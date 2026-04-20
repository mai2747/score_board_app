package com.scoreboard.app.service;

import com.scoreboard.app.Exception.ValidationException;
import com.scoreboard.app.GamePlayContext;
import com.scoreboard.app.model.*;
import com.scoreboard.app.validation.InputValidator;
import com.scoreboard.app.viewmodel.PlayerTotalScore;
import com.scoreboard.app.viewmodel.RankingDTO;
import com.scoreboard.app.viewmodel.RankingEntryDTO;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GamePlayService {
    private GroupService groupService;
    private ScoreService scoreService;
    private GameQueryService gameQueryService;
    private GamePlayContext context;

    private List<PlayerInGame> orderedPlayersInGame;
    private Map<Long, PlayerInGame> pigByPigId;
    private Map<Long, String> nameByPlayerId;

    private Game currentGame;
    private PlayerInGame currentPlayer;
    private RankingDTO currentRanking;
    private PlayerInGame previousPlayer;
    private Score previousScore;

    public int currentTurnIndex = 1;
    private int consecutiveZeroCount = 0;

    public GamePlayService(GroupService groupService, ScoreService scoreService, GameQueryService gameQueryService){
        this.groupService = groupService;
        this.scoreService = scoreService;
        this.gameQueryService = gameQueryService;
    }

    private void requireContext() {
        if (context == null) {
            throw new IllegalStateException("GamePlayContext is not set");
        }
    }

    public void submitScore(String scoreInField) throws ValidationException {
        requireContext();

        int input = InputValidator.validateScore(scoreInField);
        System.out.println("Score submitted: " + input);

        if (isConsecutiveZero(input)) {
            closeGame();
            return;
        }

        Score score = new Score(null, currentPlayer.getPigId(), currentTurnIndex, input);
        scoreService.saveScore(score);
        afterScoreChanged(true);

        context.advanceTurn();
    }

    public void editPrevScore(String scoreInField) throws ValidationException {
        requireContext();

        int input = InputValidator.validateScore(scoreInField);
        Score prevScore = context.getPreviousScore();

        System.out.println("Previous score is edited: " + prevScore + " -> " + input);

        if (isConsecutiveZero(input)) {
            closeGame();
            return;
        }

        System.out.println("PrevScore ID: " + prevScore.getScoreId());
        scoreService.editPrevScore(prevScore, input);
        afterScoreChanged(false);
    }

    private void afterScoreChanged(boolean useLastScoreAsPrevious) {
        List<Score> scores = getScores(context.getCurrentGame().getGameId());

        if (useLastScoreAsPrevious && !scores.isEmpty()) {
            context.setPreviousScore(scores.get(scores.size() - 1));
        }

        RankingDTO ranking = calculateRanking(context.getCurrentGame().getGameId());
        context.updateRanking(ranking);
    }

    private boolean isConsecutiveZero(int score) {
        if (score == 0) {
            context.incrementZeroCount();
            System.out.println("Score 0 was submitted (" + context.getConsecutiveZeroCount() +
                                "/" + context.getConsecutiveZeroThreshold() + ")");
            return context.shouldEndDueToConsecutiveZeros();
        }

        // non-zero breaks consecutive zeros
        if (context.getConsecutiveZeroCount() > 0) {
            System.out.println("Zero streak reset");
            context.resetZeroCount();
        }
        return false;
    }

    public Pair<String, Integer> createCurrentTurnInfo(){
        requireContext();
        String currentPlayerName = context.getPlayerNameByPigId(context.getCurrentPlayer().getPigId());
        int round = context.getCurrentRound();

        return new Pair<>(currentPlayerName, round);
    }


    public void closeGame() {
        requireContext();
        System.out.println("---Game ends due to consecutive zeros---");
        finishGame();
    }

    public void finishGame() {
        requireContext();
        changeGameStatus(GameStatus.FINISHED);
    }

    public void pauseGame() {
        requireContext();
        changeGameStatus(GameStatus.PAUSED);
    }

    public void resumeGame() {
        requireContext();
        changeGameStatus(GameStatus.IN_PROGRESS);
    }

    public void cancelGame() {
        requireContext();
        changeGameStatus(GameStatus.CANCELLED);

        Long groupId = context.getCurrentGame().getGroupId();
        long remaining = gameQueryService.getGameNumByGroupId(groupId);
        if (remaining == 0) {
            draftGroup();
        }
    }

    private void changeGameStatus(GameStatus newStatus) {
        context.updateGameStatus(newStatus);

        if (!newStatus.isInProgress()) {
            groupService.updateLastPlayedAt(context.getCurrentGame().getGroupId());
        }

        gameQueryService.updateStatus(context.getCurrentGame().getGameId(), newStatus);
    }

    public void activateGroup() {
        requireContext();
        changeGroupStatus(GroupStatus.ACTIVE);
    }

    public void draftGroup() {
        requireContext();
        changeGroupStatus(GroupStatus.DRAFT);
    }

    private void changeGroupStatus(GroupStatus newStatus) {
        groupService.updateStatus(context.getCurrentGame().getGroupId(), newStatus);
    }

    public void updateGameSettings(GameSettings newGameSettings) {
        requireContext();
        context.updateGameSettings(newGameSettings);
    }

    public List<PlayerTotalScore> makePlayerTotalScores(Long gameId){
        return gameQueryService.makePlayerTotalScores(gameId);
    }

    public RankingDTO calculateRanking(Long gameId){
        List<PlayerTotalScore> playerTotalScores = makePlayerTotalScores(gameId);
        return RankingService.buildRanking(gameId, playerTotalScores);
    }

    public List<RankingEntryDTO> getCurrentRanking(){
        requireContext();
        return context.getCurrentRanking().entries();
    }

    public List<Score> getCurrentScores(){
        return getScores(context.getCurrentGame().getGameId());
    }

    public List<Score> getScores(Long gameId){
        return scoreService.getScores(gameId);
    }


    public PlayerInGame getCurrentPlayer() {
        requireContext();
        return context.getCurrentPlayer();
    }

    public Game getCurrentGame() {
        requireContext();
        return context.getCurrentGame();
    }

    public Score getPrevScore() {
        requireContext();
        return context.getPreviousScore();
    }

    public String getPrevPlayerName() {
        requireContext();
        return context.getPreviousPlayerName();
    }

    public PlayerInGame getPrevPlayer() {
        requireContext();
        return context.getPreviousPlayer();
    }


    // TODO: reconsider; this method will be called off-game scene
    public String getPlayerNameByPigId(Long pigId){
        return context.getNameByPlayerId().getOrDefault(pigId, "Unknown Player");
    }

    public void setGamePlayContext(GamePlayContext context) {
        this.context = context;
    }

    public GamePlayContext getGamePlayContext() {
        return context;
    }
}
