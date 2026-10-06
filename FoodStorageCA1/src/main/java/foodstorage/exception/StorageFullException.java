package foodstorage.exception;

/**
 * Thrown when a tray is added to a storage unit that already holds its
 * maximum of 8 trays.
 *
 * <p>It is a checked exception on purpose: the caller is forced to decide what
 * to do about a full unit, which is exactly the "storage is full" case the
 * brief asks us to handle.</p>
 *
 * @author Member 1
 */
public class StorageFullException extends Exception {

    private static final long serialVersionUID = 1L;

    public StorageFullException(String message) {
        super(message);
    }

    /**
     * @param capacity the number of trays the unit holds
     * @return a ready-made exception with a message the user can understand
     */
    public static StorageFullException ofCapacity(int capacity) {
        return new StorageFullException("Storage is FULL - it already holds the maximum of "
                + capacity + " trays. Remove a tray before adding another one.");
    }
}
