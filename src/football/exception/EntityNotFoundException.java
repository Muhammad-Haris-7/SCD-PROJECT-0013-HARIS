package football.exception;

/**
 * Thrown when a requested entity is not found in the system.
 */
public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
