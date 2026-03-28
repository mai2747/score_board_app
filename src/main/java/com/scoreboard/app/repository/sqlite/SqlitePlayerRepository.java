package com.scoreboard.app.repository.sqlite;

import com.scoreboard.app.model.Player;
import com.scoreboard.app.repository.PlayerRepository;

import java.sql.*;
import java.util.Optional;

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
    public Optional<Player> findById(Long id) {
        String sql = "SELECT player_id, group_id, display_name FROM players WHERE player_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

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
}
