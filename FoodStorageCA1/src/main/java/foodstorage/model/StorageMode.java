package foodstorage.model;

/**
 * The two ways the chef is allowed to work with the storage unit.
 *
 * <p>Both modes add a tray at the front. They differ only in which end a tray
 * is taken from, which is what turns the same deque into a stack or a queue:</p>
 *
 * <ul>
 *   <li>{@link #STACK_LIFO} - add and remove on the front side, so the newest
 *       tray leaves first (last in, first out).</li>
 *   <li>{@link #QUEUE_FIFO} - add at the front, remove from the opposite side,
 *       so the oldest tray leaves first (first in, first out).</li>
 * </ul>
 *
 * @author Member 1
 */
public enum StorageMode {

    STACK_LIFO("Stack (LIFO)", "newest tray leaves first, from the front side"),
    QUEUE_FIFO("Queue (FIFO)", "oldest tray leaves first, from the back side");

    private final String displayName;
    private final String description;

    StorageMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * @return the other mode, used by menu option 8
     */
    public StorageMode other() {
        return this == STACK_LIFO ? QUEUE_FIFO : STACK_LIFO;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
