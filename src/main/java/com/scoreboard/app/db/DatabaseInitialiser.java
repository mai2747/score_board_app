package com.scoreboard.app.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitialiser {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitialiser.class);
    private static final String[] SECURITY_QUESTIONS = {
            "What is your family name?",
            "What was the name of your first pet?",
            "What city did your grandparents live in?",
            "What was the first country your family travelled to?",
            "What was the name of your primary school?"
    };

    public static void initialise(Connection connection) {
        try {
            String sql = loadSchema();

            try (Statement stmt = connection.createStatement()) {
                logger.info("Initialising database");

                for (String s : sql.split(";")) {
                    String trimmed = s.trim();
                    if (!trimmed.isEmpty()) {
                        logger.debug("Executing SQL: {}", trimmed);
                        stmt.execute(trimmed);
                    }
                }

                migrateExistingDatabase(stmt);
            }

            registerSecurityQuestions(connection);

            logger.info("Database initialisation completed");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private static void registerSecurityQuestions(Connection connection) throws SQLException {
        String sql = "INSERT OR IGNORE INTO security_questions (question_id, question_text) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < SECURITY_QUESTIONS.length; i++) {
                stmt.setInt(1, i + 1);
                stmt.setString(2, SECURITY_QUESTIONS[i]);
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    private static void migrateExistingDatabase(Statement stmt) throws SQLException {
        addAccountIdColumnIfMissing(stmt, "players", """
                UPDATE players
                SET account_id = (
                    SELECT account_id
                    FROM groups
                    WHERE groups.group_id = players.group_id
                )
                WHERE account_id IS NULL
                """);

        addAccountIdColumnIfMissing(stmt, "players_in_game", """
                UPDATE players_in_game
                SET account_id = (
                    SELECT g.account_id
                    FROM games gm
                    JOIN groups g
                      ON g.group_id = gm.group_id
                    WHERE gm.game_id = players_in_game.game_id
                )
                WHERE account_id IS NULL
                """);
    }

    private static void addAccountIdColumnIfMissing(Statement stmt, String tableName, String backfillSql) throws SQLException {
        if (!columnExists(stmt, tableName, "account_id")) {
            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN account_id INTEGER REFERENCES accounts(account_id) ON DELETE CASCADE");
        }

        stmt.executeUpdate(backfillSql);
    }

    private static boolean columnExists(Statement stmt, String tableName, String columnName) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + tableName + ")")) {
            while (rs.next()) {
                if (columnName.equals(rs.getString("name"))) {
                    return true;
                }
            }
        }

        return false;
    }

    private static String loadSchema() throws Exception {

        InputStream is = DatabaseInitialiser.class
                .getClassLoader()
                .getResourceAsStream("db/schema.sql");

        if (is == null) {
            throw new RuntimeException("schema.sql not found");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {

            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
