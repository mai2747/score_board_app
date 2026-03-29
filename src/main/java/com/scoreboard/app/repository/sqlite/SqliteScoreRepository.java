package com.scoreboard.app.repository.sqlite;

import com.scoreboard.app.model.Score;
import com.scoreboard.app.repository.ScoreRepository;
import com.scoreboard.app.viewmodel.PlayerTotalScore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteScoreRepository implements ScoreRepository {
    private final Connection conn;

    public SqliteScoreRepository(Connection conn){
        this.conn = conn;
    }

    @Override
    public void save(Score score) {
        if(score == null){
            throw new IllegalArgumentException("score is null");
        }

        if(score.getScoreId() == null){
            long generatedId = insert(score);
            score.setScoreId(generatedId);
        }else{
            update(score);
        }
    }

    public long insert(Score score){
        String sql = "INSERT INTO scores (player_in_game_id, turn_number, score) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setLong(1, score.getPlayerInGameId());
            stmt.setInt(2, score.getTurnNumber());
            stmt.setInt(3, score.getScore());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new RuntimeException("Failed to get generated id");
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Failed to insert score", e);
        }
    }

    @Override
    public void update(Score score) {
        String sql = "UPDATE scores SET score = ? WHERE score_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, score.getScore());
            stmt.setLong(2, score.getScoreId());

            stmt.executeUpdate();

        }catch(SQLException e){
            throw new RuntimeException("Update score failed", e);
        }
    }

    // TODO: Should not be used, replace with findByGameId for memory uses
    @Override
    public List<Score> getScores() {
        return List.of();
    }

    @Override
    public void clearGameScores(Long gameId) {
        String sql = """
                    DELETE FROM scores
                    WHERE player_in_game_id IN (
                        SELECT player_in_game_id
                        FROM players_in_game
                        WHERE game_id = ?
                    )
                    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, gameId);

            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Delete scores by game id failed", e);
        }
    }

    @Override
    public Optional<Score> findLatestByGameId(Long gameId) {
        String sql = """
                    SELECT s.score_id, s.player_in_game_id, s.turn_number, s.score
                    FROM scores s
                    JOIN players_in_game pig
                      ON s.player_in_game_id = pig.player_in_game_id
                    WHERE pig.game_id = ?
                    ORDER BY s.turn_number DESC, s.score_id DESC
                    LIMIT 1
                    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Score score = new Score(
                            rs.getLong("score_id"),
                            rs.getLong("player_in_game_id"),
                            rs.getInt("turn_number"),
                            rs.getInt("score")
                    );
                    return Optional.of(score);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find latest score by game id failed", e);
        }
    }

    @Override
    public List<Score> findScoresByGameId(Long gameId) {
        String sql = """
                    SELECT s.score_id, s.player_in_game_id, s.turn_number, s.score
                    FROM scores s
                    JOIN players_in_game pig
                      ON s.player_in_game_id = pig.player_in_game_id
                    WHERE pig.game_id = ?
                    ORDER BY s.turn_number ASC, pig.turn_order ASC
                    """;

        List<Score> scores = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Score score = new Score(
                            rs.getLong("score_id"),
                            rs.getLong("player_in_game_id"),
                            rs.getInt("turn_number"),
                            rs.getInt("score")
                    );

                    scores.add(score);
                }

                return scores;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find scores by game id failed", e);
        }
    }

    @Override
    public List<PlayerTotalScore> findPlayerTotalScoresByGameId(Long gameId){
        String sql = """
                    SELECT pig.player_id, p.display_name, SUM(s.score) AS total_score
                    FROM scores s
                    JOIN players_in_game pig
                      ON s.player_in_game_id = pig.player_in_game_id
                    JOIN players p
                      ON pig.player_id = p.player_id
                    WHERE pig.game_id = ?
                    GROUP BY pig.player_id, p.display_name
                    ORDER BY total_score DESC, LOWER(p.display_name) ASC, pig.player_id ASC
                    """;

        List<PlayerTotalScore> totals = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
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

        }catch (SQLException e){
            throw new RuntimeException("getting player total score failed", e);
        }
    }

    // Extension for score editing for any of past score
    @Override
    public Score findByScoreId(Long ScoreId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
