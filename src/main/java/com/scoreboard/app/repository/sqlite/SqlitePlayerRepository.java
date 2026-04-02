package com.scoreboard.app.repository.sqlite;

import com.scoreboard.app.model.Player;
import com.scoreboard.app.repository.PlayerRepository;
import com.scoreboard.app.viewmodel.PlayerWinRateDTO;

import java.sql.*;
import java.util.*;

public class SqlitePlayerRepository implements PlayerRepository {
    private final Connection conn;

    public SqlitePlayerRepository(Connection conn) {
        this.conn = conn;
    }

    @Override
    public long reserveId() {
        throw new UnsupportedOperationException("reserveId is not used in SQLite repository");
    }

    //1
    @Override
    public Player save(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("player is null");
        }

        if (player.getId() == null) {
            long generatedId = insert(player);
            player.setId(generatedId);
        } else {
            update(player);
        }

        return player;
    }

    private long insert(Player player){
        String sql = "INSERT INTO players (group_id, display_name) VALUES (?, ?)";

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, player.getGroupId());
            stmt.setString(2, player.getName());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new RuntimeException("Failed to get generated id");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Insert player failed", e);
        }
    }

    @Override
    public void update(Player player) {
        String sql = "UPDATE players SET display_name = ? WHERE player_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, player.getName());
            stmt.setLong(2, player.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Update player failed", e);
        }
    }

    // TODO: No usage for methods below? -> delete if not needed

    @Override
    public Optional<Player> findByPlayerId(Long playerId) {
        String sql = "SELECT player_id, group_id, display_name FROM players WHERE player_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, playerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Player(
                            rs.getLong("player_id"),
                            rs.getLong("group_id"),
                            rs.getString("display_name")
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find player by id failed", e);
        }
    }

    @Override
    public List<Player> findByGroupId(Long groupId){
        String sql = "SELECT player_id, group_id, display_name FROM players WHERE group_id = ?";

        List<Player> players = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, groupId);

             try (ResultSet rs = stmt.executeQuery()) {

                 while (rs.next()) {

                     Player player = new Player(
                             rs.getLong("player_id"),
                             rs.getLong("group_id"),
                             rs.getString("display_name")
                     );

                     players.add(player);
                 }

                 return players;
             }

        } catch (SQLException e) {
            throw new RuntimeException("Find players in a group failed", e);
        }
    }

    @Override
    public Optional<Player> findByGroupIdAndName(Long groupId, String name) {
        String sql = "SELECT player_id, group_id, display_name FROM players WHERE group_id = ? AND display_name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, groupId);
            stmt.setString(2, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Player(
                            rs.getLong("player_id"),
                            rs.getLong("group_id"),
                            rs.getString("display_name")
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find player by group id and name failed", e);
        }
    }

    public Map<Long, PlayerWinRateDTO> findPlayerWinRatesByGroupId(Long groupId) {
        String sql = """
                    WITH player_game_total AS (
                        SELECT
                            pig.game_id,
                            pig.player_id,
                            SUM(s.score) AS total_score
                        FROM players_in_game pig
                        JOIN scores s
                          ON s.player_in_game_id = pig.player_in_game_id
                        JOIN games g
                          ON g.game_id = pig.game_id
                        WHERE g.status = 'FINISHED'
                          AND g.group_id = ?
                        GROUP BY pig.game_id, pig.player_id
                    ),
                    game_max_score AS (
                        SELECT
                            game_id,
                            MAX(total_score) AS max_score
                        FROM player_game_total
                        GROUP BY game_id
                    ),
                    game_winners AS (
                        SELECT
                            pgt.game_id,
                            pgt.player_id,
                            CASE
                                WHEN pgt.total_score = gms.max_score THEN 1
                                ELSE 0
                            END AS is_win
                        FROM player_game_total pgt
                        JOIN game_max_score gms
                          ON pgt.game_id = gms.game_id
                    ),
                    player_stats AS (
                        SELECT
                            gw.player_id,
                            COUNT(*) AS games_played,
                            SUM(gw.is_win) AS wins,
                            CAST(SUM(gw.is_win) AS REAL) / COUNT(*) AS win_rate
                        FROM game_winners gw
                        GROUP BY gw.player_id
                    )
                    SELECT
                        p.player_id,
                        p.display_name,
                        COALESCE(ps.games_played, 0) AS games_played,
                        COALESCE(ps.wins, 0) AS wins,
                        CASE
                            WHEN COALESCE(ps.games_played, 0) = 0 THEN 0.0
                            ELSE ps.win_rate
                        END AS win_rate
                    FROM players p
                    LEFT JOIN player_stats ps
                      ON p.player_id = ps.player_id
                    WHERE p.group_id = ?
                    ORDER BY win_rate DESC, wins DESC, LOWER(p.display_name) ASC
                    """;

        Map<Long, PlayerWinRateDTO> resultMap = new HashMap<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, groupId);
            stmt.setLong(2, groupId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PlayerWinRateDTO dto = new PlayerWinRateDTO(
                            rs.getLong("player_id"),
                            rs.getString("display_name"),
                            rs.getInt("games_played"),
                            rs.getInt("wins"),
                            rs.getDouble("win_rate")
                    );
                    resultMap.put(dto.playerId(), dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get player win rates", e);
        }

        return resultMap;
    }
}
