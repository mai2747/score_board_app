package com.scoreboard.app;

import com.scoreboard.app.model.*;
import com.scoreboard.app.viewmodel.RankingDTO;

import java.util.List;
import java.util.Map;

public class GamePlayContext {
    private final Game currentGame;
    private final List<PlayerInGame> orderedPlayersInGame;
    private final Map<Long, PlayerInGame> pigByPigId;
    private final Map<Long, String> nameByPlayerId;
    private final int playerNum;

    private PlayerInGame currentPlayer;
    private PlayerInGame previousPlayer;
    private Score previousScore;
    private RankingDTO currentRanking;
    private int currentTurnIndex;
    private int consecutiveZeroCount;

    private GamePlayContext(
            Game currentGame,
            List<PlayerInGame> orderedPlayersInGame,
            Map<Long, PlayerInGame> pigByPigId,
            Map<Long, String> nameByPlayerId,
            int playerNum,
            PlayerInGame currentPlayer,
            PlayerInGame previousPlayer,
            Score previousScore,
            RankingDTO currentRanking,
            int currentTurnIndex,
            int consecutiveZeroCount
    ) {
        this.currentGame = currentGame;
        this.orderedPlayersInGame = orderedPlayersInGame;
        this.pigByPigId = pigByPigId;
        this.nameByPlayerId = nameByPlayerId;
        this.playerNum = playerNum;
        this.currentPlayer = currentPlayer;
        this.previousPlayer = previousPlayer;
        this.previousScore = previousScore;
        this.currentRanking = currentRanking;
        this.currentTurnIndex = currentTurnIndex;
        this.consecutiveZeroCount = consecutiveZeroCount;
    }

    public static GamePlayContext forNewGame(
            Game game,
            List<PlayerInGame> players,
            Map<Long, PlayerInGame> pigByPigId,
            Map<Long, String> nameByPlayerId
    ) {
        return new GamePlayContext(
                game,
                players,
                pigByPigId,
                nameByPlayerId,
                players.size(),
                players.get(0),
                null,
                null,
                null,
                1,
                0
        );
    }

    public static GamePlayContext forResumedGame(
            Game game,
            List<PlayerInGame> players,
            Map<Long, PlayerInGame> pigByPigId,
            Map<Long, String> nameByPlayerId,
            PlayerInGame currentPlayer,
            PlayerInGame previousPlayer,
            Score previousScore,
            RankingDTO currentRanking,
            int currentTurnIndex,
            int consecutiveZeroCount
    ) {
        return new GamePlayContext(
                game,
                players,
                pigByPigId,
                nameByPlayerId,
                players.size(),
                currentPlayer,
                previousPlayer,
                previousScore,
                currentRanking,
                currentTurnIndex,
                consecutiveZeroCount
        );
    }

    public void advanceTurn() {
        this.previousPlayer = this.currentPlayer;

        int nextIndex = this.currentTurnIndex % orderedPlayersInGame.size();
        this.currentPlayer = orderedPlayersInGame.get(nextIndex);

        this.currentTurnIndex++;
    }

    public void setPreviousScore(Score score) {
        this.previousScore = score;
    }

    public void updateRanking(RankingDTO ranking) {
        this.currentRanking = ranking;
    }

    public void incrementZeroCount() {
        this.consecutiveZeroCount++;
    }

    public void resetZeroCount() {
        this.consecutiveZeroCount = 0;
    }

    public void updateGameSettings(GameSettings settings) {
        this.currentGame.setSettings(settings);
    }

    public void updateGameStatus(GameStatus status) {
        this.currentGame.setGameStatus(status);
    }

    public String getPlayerNameByPigId(Long pigId) {
        return nameByPlayerId.getOrDefault(pigId, "Unknown Player");
    }

    public String getPreviousPlayerName() {
        if (previousPlayer == null) {
            return "";
        }
        return getPlayerNameByPigId(previousPlayer.getPigId());
    }

    public int getCurrentRound() {
        return ((currentTurnIndex - 1) / orderedPlayersInGame.size()) + 1;
    }

    public int getConsecutiveZeroThreshold() {
        return orderedPlayersInGame.size() * 3;
    }

    public boolean shouldEndDueToConsecutiveZeros() {
        return consecutiveZeroCount >= getConsecutiveZeroThreshold();
    }

    // Getter without business logics

    public Game getCurrentGame() {
        return currentGame;
    }

    public List<PlayerInGame> getOrderedPlayersInGame() {
        return orderedPlayersInGame;
    }

    public Map<Long, PlayerInGame> getPigByPigId() {
        return pigByPigId;
    }

    public Map<Long, String> getNameByPlayerId() {
        return nameByPlayerId;
    }

    public PlayerInGame getCurrentPlayer() {
        return currentPlayer;
    }

    public PlayerInGame getPreviousPlayer() {
        return previousPlayer;
    }

    public Score getPreviousScore() {
        return previousScore;
    }

    public RankingDTO getCurrentRanking() {
        return currentRanking;
    }

    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    public int getConsecutiveZeroCount() {
        return consecutiveZeroCount;
    }

    public int getPlayerCount() {
        return orderedPlayersInGame.size();
    }
}