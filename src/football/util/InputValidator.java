package football.util;

import football.exception.ValidationException;

/**
 * Utility class for validating user inputs.
 * Centralises all validation logic (refactoring: no duplication).
 */
public class InputValidator {

    private InputValidator() { /* Utility class – no instantiation */ }

    /** Validates a non-empty name field */
    public static void validateName(String name, String fieldLabel) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException(fieldLabel + " cannot be empty.");
        }
        if (name.trim().length() < 2) {
            throw new ValidationException(fieldLabel + " must be at least 2 characters.");
        }
        if (name.trim().length() > 60) {
            throw new ValidationException(fieldLabel + " must be 60 characters or fewer.");
        }
    }

    /** Validates player age is within realistic football range */
    public static void validateAge(int age) throws ValidationException {
        if (age < 15 || age > 45) {
            throw new ValidationException("Age must be between 15 and 45.");
        }
    }

    /** Validates a score value is non-negative */
    public static void validateScore(int score) throws ValidationException {
        if (score < 0) {
            throw new ValidationException("Score cannot be negative.");
        }
    }

    /** Validates a stat value (goals, assists, cards) is non-negative */
    public static void validateStat(int value, String statLabel) throws ValidationException {
        if (value < 0) {
            throw new ValidationException(statLabel + " cannot be negative.");
        }
    }

    /** Parses and returns an int from a string, throwing a user-friendly message on failure */
    public static int parseIntField(String text, String fieldLabel) throws ValidationException {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldLabel + " must be a whole number.");
        }
    }
}
