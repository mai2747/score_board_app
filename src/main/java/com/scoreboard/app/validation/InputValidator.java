package com.scoreboard.app.validation;

import com.scoreboard.app.Exception.ValidationException;

public final class InputValidator {

    private static final int SCORE_MIN = 0;
    private static final int SCORE_MAX = 999;

    private static final int PLAYER_NAME_MIN = 1;
    private static final int PLAYER_NAME_MAX = 20;

    private static final int GROUP_NAME_MIN = 1;
    private static final int GROUP_NAME_MAX = 30;

    private static final int ACCOUNT_NAME_MIN = 1;
    private static final int ACCOUNT_NAME_MAX = 30;

    private static final String NAME_PATTERN = "[A-Za-z0-9 _-]+";

    private InputValidator() {
    }

    public static int validateScore(String input) throws ValidationException {
        if (input == null) {
            throw new ValidationException("Score is required.");
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException("Score is required.");
        }
        if (trimmed.contains(" ")) {
            throw new ValidationException("Spaces are not allowed.");
        }
        if (!trimmed.matches("[0-9]+")) {
            throw new ValidationException("Score must be a half-width integer.");
        }

        int value = Integer.parseInt(trimmed);
        if (value < SCORE_MIN || value > SCORE_MAX) {
            throw new ValidationException("Score must be between 0 and 999.");
        }

        return value;
    }

    public static String validatePlayerName(String input) throws ValidationException {
        return validateName(input, PLAYER_NAME_MIN, PLAYER_NAME_MAX, "Player name");
    }

    public static String validateGroupName(String input) throws ValidationException {
        return validateName(input, GROUP_NAME_MIN, GROUP_NAME_MAX, "Group name");
    }

    public static String validateAccountName(String input) throws ValidationException {
        if(input == null) throw new ValidationException("Account name must not be null");
        return validateName(input, ACCOUNT_NAME_MIN, ACCOUNT_NAME_MAX, "Account name");
    }

    private static String validateName(String input, int min, int max, String fieldName) throws ValidationException {
        if (input == null) {
            System.out.println(fieldName + " is set as a default");
            return null;
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException(fieldName + " is required.");
        }
        if (trimmed.length() < min || trimmed.length() > max) {
            throw new ValidationException(fieldName + " must be between " + min + " and " + max + " characters.");
        }
        if (!trimmed.matches(NAME_PATTERN)) {
            throw new ValidationException(fieldName + " contains invalid characters.");
        }

        return trimmed;
    }
}