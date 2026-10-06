package foodstorage.exception;

/**
 * Thrown when an operation needs at least one tray but the storage unit is
 * empty: remove, peek, display all, or any of the three searches.
 *
 * <p>This is the "error handling when there is no data" requirement in the
 * brief. The menu catches it and prints a message, so the application keeps
 * running instead of crashing.</p>
 *
 * @author Member 1
 */
public class StorageEmptyException extends Exception {

    private static final long serialVersionUID = 1L;

    public StorageEmptyException(String message) {
        super(message);
    }

    /**
     * @param operation what the user was trying to do, e.g. "remove a tray"
     * @return a ready-made exception naming the operation that failed
     */
    public static StorageEmptyException forOperation(String operation) {
        return new StorageEmptyException("Storage is EMPTY - cannot " + operation
                + ". Add a tray first (option 1).");
    }
}
