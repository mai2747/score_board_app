package com.scoreboard.app.viewmodel;

import java.util.List;

public record RankingDTO(
        Long gameId,
        List<RankingEntryDTO> entries
) {}
