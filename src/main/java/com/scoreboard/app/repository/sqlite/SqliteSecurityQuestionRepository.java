package com.scoreboard.app.repository.sqlite;

import com.scoreboard.app.model.SecQuestion;
import com.scoreboard.app.repository.SecurityQuestionRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqliteSecurityQuestionRepository implements SecurityQuestionRepository {

    private final Connection connection;

    public SqliteSecurityQuestionRepository(Connection connection) {
        this.connection = connection;
    }

    public List<SecQuestion> findAll() {
        List<SecQuestion> list = new ArrayList<>();
        String sql = "SELECT question_id, question_text FROM security_questions ORDER BY question_id";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new SecQuestion(
                        rs.getInt("question_id"),
                        rs.getString("question_text")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find security questions failed", e);
        }

        return list;
    }
}