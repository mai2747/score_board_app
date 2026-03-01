package com.scoreboard.app.dto;

import java.util.List;

public record RankingDTO(
        Long gameId,
        List<RankingEntryDTO> entries
) {}
