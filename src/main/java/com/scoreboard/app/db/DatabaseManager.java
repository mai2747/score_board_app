package com.scoreboard.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:scrabble.db";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }

            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }
}