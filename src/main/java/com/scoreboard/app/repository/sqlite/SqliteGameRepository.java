package com.scoreboard.app.repository.sqlite;

import com.scoreboard.app.model.Game;
import com.scoreboard.app.model.GameStatus;
import com.scoreboard.app.repository.GameRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteGameRepository implements GameRepository {
    private final Connection conn;

    public SqliteGameRepository(Connection conn){
        this.conn = conn;
    }


    @Override
    public Game save(Game game) {
        if(game == null){
            throw new IllegalArgumentException("game is null");
        }

        String sql = "INSERT INTO games (group_id, status, rule_version) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            stmt.setLong(1, game.getGroupId());
            stmt.setString(2, game.getGameStatus().name());
            stmt.setString(3, game.getGameRule());  // might be changed and used in future expansion

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long generatedId = rs.getLong(1);
                    game.setGameID(generatedId);
                } else {
                    throw new RuntimeException("Failed to get generated id");
                }
            }

        }catch (SQLException e){
            throw new RuntimeException("Failed to get generated id", e);
        }

        return game;
    }

    @Override
    public void updateStatus(Long gameId, GameStatus newStatus){
        String sql = "UPDATE games SET status = ? WHERE game_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            stmt.setLong(2, gameId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Update game status failed", e);
        }
    }

    @Override
    public long countByGroupId(Long groupId) {
        String sql = "SELECT COUNT(*) FROM games WHERE group_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1,groupId);

            try (ResultSet rs = stmt.executeQuery()){
                rs.next();
                return rs.getLong(1);
            }
        }catch (SQLException e){
            throw new RuntimeException("Count games failed", e);
        }
    }

    @Override
    public void delete(Long gameId) {
        String sql = "DELETE FROM games WHERE game_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, gameId);

            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Delete game by game id failed", e);
        }
    }

    @Override
    public Optional<Game> findById(Long id) {
        String sql = "SELECT game_id, group_id, status, rule_version FROM games WHERE game_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Game(
                            rs.getLong("game_id"),
                            rs.getLong("group_id"),
                            GameStatus.valueOf(rs.getString("status")),
                            rs.getString("rule_version")
                    ));
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find game by id failed", e);
        }
    }

    @Override
    public List<Game> findAll() {
        String sql = "SELECT game_id, group_id, status, rule_version FROM games";

        List<Game> games = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)){

            try (ResultSet rs = stmt.executeQuery()){

                while(rs.next()){
                    Game game = new Game(
                            rs.getLong("game_id"),
                            rs.getLong("group_id"),
                            GameStatus.valueOf(rs.getString("status")),
                            rs.getString("rule_version")
                    );
                    games.add(game);
                }

                return games;
            }
        }catch (SQLException e){
            throw new RuntimeException("Find all games failed", e);
        }
    }

    @Override
    public List<Game> findAllByStatus(GameStatus status) {
        String sql = "SELECT game_id, group_id, status, rule_version FROM games WHERE status = ?";

        List<Game> games = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Game game = new Game(
                            rs.getLong("game_id"),
                            rs.getLong("group_id"),
                            GameStatus.valueOf(rs.getString("status")),
                            rs.getString("rule_version")
                    );
                    games.add(game);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find games by status failed", e);
        }

        return games;
    }

    @Override
    public boolean existsByStatus(GameStatus status) {
        String sql = "SELECT 1 FROM games WHERE status = ? LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Check game status existence failed", e);
        }
    }

    @Override
    public void updateStatusByCurrentStatus(GameStatus from, GameStatus to) {
        String sql = "UPDATE games SET status = ? WHERE status = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, to.name());
            stmt.setString(2, from.name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Bulk update game status failed", e);
        }
    }
}
