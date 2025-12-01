//package com.scoreboard.app.repository;
//
//import com.scoreboard.app.model.Player;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.Statement;
//
//public class PlayerRepository {
//
//    public Player save(Player player) {
//        String sql = "INSERT INTO players (group_id, name) VALUES (?, ?)";
//
//        try (PreparedStatement stmt = connection.prepareStatement(
//                sql, Statement.RETURN_GENERATED_KEYS)) {
//
//            stmt.setLong(1, player.getGroupId());
//            stmt.setString(2, player.getName());
//            stmt.executeUpdate();
//
//            // DB が自動生成した ID を取得する
//            ResultSet keys = stmt.getGeneratedKeys();
//            if (keys.next()) {
//                long id = keys.getLong(1);
//                player.setId(id);  // ← ここで Player オブジェクトに ID をセット
//            }
//
//            return player;
//        }
//    }
//
//}
