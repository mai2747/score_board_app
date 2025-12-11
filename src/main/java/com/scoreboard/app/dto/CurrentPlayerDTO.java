package com.scoreboard.app.dto;

public record CurrentPlayerDTO(
        Long playerId,
        String name,
        int turnOrder
) {}