package com.scoreboard.app.viewmodel;

public record PlayerWinRateDTO(
        Long playerId,
        String playerName,
        int gamesPlayed,
        int wins,
        double winRate
) {}