package com.scoreboard.app.service;

import com.scoreboard.app.viewmodel.PlayerTotalScore;
import com.scoreboard.app.viewmodel.RankingDTO;
import com.scoreboard.app.viewmodel.RankingEntryDTO;
import com.scoreboard.app.model.Score;

import java.util.*;

public class RankingService {

    public RankingDTO buildRanking(Long gameId, List<PlayerTotalScore> totals) {
        List<PlayerTotalScore> sorted = new ArrayList<>(totals);
        sorted.sort(
                Comparator.<PlayerTotalScore>comparingInt(PlayerTotalScore::totalScore)
                        .reversed()
                        .thenComparing(t -> t.playerName() == null ? "" : t.playerName(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(PlayerTotalScore::playerId)
        );

        List<RankingEntryDTO> entries = getRankingEntryDTOS(sorted);

        return new RankingDTO(gameId, entries);
    }

    private static List<RankingEntryDTO> getRankingEntryDTOS(List<PlayerTotalScore> sorted) {
        List<RankingEntryDTO> entries = new ArrayList<>();
        int rank = 0;
        int sameRankCount = 0;
        Integer prevScore = null;

        for (PlayerTotalScore row : sorted) {
            int total = row.totalScore();

            if (prevScore == null || total != prevScore) {
                rank = rank + 1 + sameRankCount;
                sameRankCount = 0;
                prevScore = total;
            } else {
                sameRankCount++;
            }

            String name = row.playerName() == null ? "(unknown)" : row.playerName();
            entries.add(new RankingEntryDTO(rank, row.playerId(), name, total));
        }
        return entries;
    }
}