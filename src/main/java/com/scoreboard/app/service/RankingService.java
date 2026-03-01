package com.scoreboard.app.service;

import com.scoreboard.app.dto.RankingDTO;
import com.scoreboard.app.dto.RankingEntryDTO;
import com.scoreboard.app.model.Score;

import java.util.*;

public class RankingService {

    public RankingDTO buildRanking(Long gameID, List<Score> scores, Map<Long, String> nameByPlayerId){
        // Sum up scores for each player
        Map<Long, Integer> totalByPlayer = new HashMap<>();
        for (Score s : scores) {
            if (s == null) continue;
            Long playerID = s.getPlayerId();
            if (playerID == null) continue;

            int value = s.getScore();
            totalByPlayer.merge(playerID, value, Integer::sum); //なん
        }

        // Sort totalByPlayer by scores
        List<Map.Entry<Long, Integer>> sorted = new ArrayList<>(totalByPlayer.entrySet());
        sorted.sort(
                Comparator.<Map.Entry<Long, Integer>>comparingInt(e -> e.getValue())
                        .reversed()
                        .thenComparing(e -> nameByPlayerId.getOrDefault(e.getKey(), ""), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(e -> e.getKey())
        );

        // Build ranking
        List<RankingEntryDTO> entries = new ArrayList<>();
        int rank = 0;
        int sameRankCount = 0;
        Integer prevScore = null;

        for (int i = 0; i < sorted.size(); i++) {
            Long playerId = sorted.get(i).getKey();
            int total = sorted.get(i).getValue();

            if (prevScore == null || total != prevScore) {
                rank = rank + 1 + sameRankCount;      // dense ranking increments when score changes
                sameRankCount = 0;
                prevScore = total;
            }else{
                sameRankCount++;
            }

            String name = nameByPlayerId.getOrDefault(playerId, "(unknown)");
            entries.add(new RankingEntryDTO(rank, playerId, name, total));
        }
        return new RankingDTO(gameID, entries);
    }
}

/*
RankingEntryDTO(
        int rank,
        Long playerId,
        String playerName,
        int totalScore
 */