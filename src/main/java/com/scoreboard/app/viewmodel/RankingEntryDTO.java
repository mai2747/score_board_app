package com.scoreboard.app.viewmodel;

public record RankingEntryDTO(
        int rank,
        Long playerId,
        String playerName,
        int totalScore
) {}