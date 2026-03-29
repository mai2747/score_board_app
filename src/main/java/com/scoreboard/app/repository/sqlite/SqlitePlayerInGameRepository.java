package com.scoreboard.app.repository.sqlite;

import com.scoreboard.app.model.PlayerInGame;
import com.scoreboard.app.repository.PlayerInGameRepository;
import com.scoreboard.app.viewmodel.PlayerTotalScore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlitePlayerInGameRepository implements PlayerInGameRepository {
    private final Connection conn;

    public SqlitePlayerInGameRepository(Connection conn){
        this.conn = conn;
    }

    @Override
    public PlayerInGame save(PlayerInGame pig) {
        if(pig == null){
            throw new IllegalArgumentException("player_in_game is null");
        }

        long generatedId = insert(pig);
        pig.setPigId(generatedId);

        return pig;
    }

    @Override
    public Optional<PlayerInGame> findById(Long playerInGameId) {
        String sql = """
                    SELECT player_in_game_id, game_id, player_id, turn_order 
                    FROM players_in_game 
                    WHERE player_in_game_id = ? 
                    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, playerInGameId);

            try (ResultSet rs = stmt.executeQuery()){

                if (rs.next()) {
                    return Optional.of( new PlayerInGame(
                            rs.getLong("player_in_game_id"),
                            rs.getLong("game_id"),
                            rs.getLong("player_id"),
                            rs.getInt("turn_order")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find players in game failed", e);
        }

        return Optional.empty();
    }

    private long insert(PlayerInGame pig){
        String sql = "INSERT INTO players_in_game (game_id, player_id, turn_order) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setLong(1, pig.getGameId());
            stmt.setLong(2, pig.getPlayerId());
            stmt.setInt(3, pig.getTurnOrder());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()){
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new RuntimeException("Failed to get generated id");
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Insert PlayerInGame failed", e);
        }
    }

    @Override
    public List<PlayerInGame> findPlayersByGameId(Long gameId) {
        String sql = """
                    SELECT player_in_game_id, game_id, player_id, turn_order 
                    FROM players_in_game 
                    WHERE game_id = ? 
                    ORDER BY turn_order ASC
                    """;

        List<PlayerInGame> pigs = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
             stmt.setLong(1, gameId);

             try (ResultSet rs = stmt.executeQuery()){

                while (rs.next()) {
                    PlayerInGame pig = new PlayerInGame(
                            rs.getLong("player_in_game_id"),
                            rs.getLong("game_id"),
                            rs.getLong("player_id"),
                            rs.getInt("turn_order")
                    );

                    pigs.add(pig);
                }
                return pigs;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Find players in game failed", e);
        }
    }

    @Override
    public List<PlayerTotalScore> findPlayerTotalScoreByGameId(Long gameId) {
        String sql = """
                    SELECT pig.player_id,
                           p.display_name,
                           COALESCE(SUM(s.score), 0) AS total_score
                    FROM players_in_game pig
                    JOIN players p
                      ON pig.player_id = p.player_id
                    LEFT JOIN scores s
                      ON s.player_in_game_id = pig.player_in_game_id
                    WHERE pig.game_id = ?
                    GROUP BY pig.player_id, p.display_name, pig.player_in_game_id
                    ORDER BY total_score DESC, LOWER(p.display_name) ASC, pig.player_id ASC
                    """;

        List<PlayerTotalScore> totals = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PlayerTotalScore total = new PlayerTotalScore(
                            rs.getLong("player_id"),
                            rs.getString("display_name"),
                            rs.getInt("total_score")
                    );
                    totals.add(total);
                }
                return totals;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getting player total score failed", e);
        }
    }
}
