package com.scoreboard.app.repository.sqlite;

import com.scoreboard.app.model.Game;
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
            stmt.setString(2, game.getGameStatus());
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
    public Optional<Game> findById(Long id) {
        String sql = "SELECT game_id, group_id, status, rule_version FROM games WHERE game_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Game(
                            rs.getLong("game_id"),
                            rs.getLong("group_id"),
                            rs.getString("status"),
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
                            rs.getString("status"),
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
}
