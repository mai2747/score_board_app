package com.scoreboard.app.viewmodel;

public record PlayerTotalScore(
        Long playerId,
        String playerName,
        int totalScore
) {}
