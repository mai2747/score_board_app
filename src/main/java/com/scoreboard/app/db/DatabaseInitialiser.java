package com.scoreboard.app.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitialiser {

    public static void initialise(Connection connection) {
        try {
            String sql = loadSchema();

            try (Statement stmt = connection.createStatement()) {
                System.out.println("Initialising Database");

                for (String s : sql.split(";")) {
                    String trimmed = s.trim();
                    if (!trimmed.isEmpty()) {
                        System.out.println("Executing SQL:\n" + trimmed);
                        stmt.execute(trimmed);
                    }
                }
            }

            System.out.println("...Completed!");
            System.out.println();

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
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