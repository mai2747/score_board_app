package com.scoreboard.app.dto;

public record RankingEntryDTO(
        int rank,
        Long playerId,
        String playerName,
        int totalScore
) {}