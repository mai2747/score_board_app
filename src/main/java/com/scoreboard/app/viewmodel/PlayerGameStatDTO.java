package com.scoreboard.app.viewmodel;

public record PlayerGameStatDTO(
        Long playerId,
        String playerName,
        int gameIndex,
        int totalScore,
        int rank
) {}