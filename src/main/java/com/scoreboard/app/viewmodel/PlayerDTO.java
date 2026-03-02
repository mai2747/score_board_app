package com.scoreboard.app.viewmodel;

// This DTO will be used as a cache. Mainly for UI.
// Using a list of PlayerDTO, the system won't need to refer playerName/order from DB (Player entity itself)

public record PlayerDTO(
        Long playerId,
        String name,
        int turnOrder
) {}