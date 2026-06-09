package football.exception;

/**
 * Thrown when input validation fails.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
