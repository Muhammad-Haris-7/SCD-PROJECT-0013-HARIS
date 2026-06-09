package football.exception;

/**
 * Thrown when a duplicate entity (player/team) is added.
 */
public class DuplicateEntryException extends Exception {
    public DuplicateEntryException(String message) {
        super(message);
    }
}
