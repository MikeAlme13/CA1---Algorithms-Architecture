package foodstorage.datastructure;

import foodstorage.exception.StorageEmptyException;
import foodstorage.exception.StorageFullException;
import foodstorage.model.FoodItem;
import foodstorage.model.StorageMode;

import java.util.Collections;
import java.util.List;

/**
 * The party food storage unit: a {@link CircularDeque} of 8 trays plus the
 * rules the chef works by.
 *
 * <h2>The chef's two options</h2>
 * <p>A tray is <em>always</em> added at the front, because the front is the
 * side the chef loads from in both of the options the brief allows. Only the
 * removal end depends on the mode:</p>
 *
 * <ul>
 *   <li>{@link StorageMode#STACK_LIFO} - remove from the front, so the newest
 *       tray leaves first.</li>
 *   <li>{@link StorageMode#QUEUE_FIFO} - remove from the back, so the oldest
 *       tray leaves first.</li>
 * </ul>
 *
 * <p>Switching mode moves no tray at all: it only changes which end
 * {@link #remove()} reads. That is the whole advantage of using a deque rather
 * than a separate stack and queue.</p>
 *
 * <h2>Layering</h2>
 * <p>This class is the only place that touches the deque. It translates the
 * deque's standard exceptions into the project's own
 * {@link StorageFullException} and {@link StorageEmptyException}, so the
 * {@code ui} package never sees an array, an index or a
 * {@code NoSuchElementException}.</p>
 *
 * @author Member 1
 */
public class FoodStorage {

    /** The unit holds at most 8 trays, as stated in the brief. */
    public static final int CAPACITY = 8;

    private final CircularDeque<FoodItem> trays;
    private StorageMode mode;

    /**
     * Creates an empty unit in the given mode.
     *
     * @param mode the chef's working mode, not null
     * @throws IllegalArgumentException if mode is null
     */
    public FoodStorage(StorageMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Storage mode cannot be null.");
        }
        this.trays = new CircularDeque<>(CAPACITY);
        this.mode = mode;
    }

    /** Creates an empty unit in the recommended Queue (FIFO) mode. */
    public FoodStorage() {
        this(StorageMode.QUEUE_FIFO);
    }

    /**
     * Adds a tray at the front of the unit. O(1).
     *
     * @param item the tray to store, not null
     * @throws StorageFullException     if the unit already holds 8 trays
     * @throws IllegalArgumentException if item is null
     */
    public void add(FoodItem item) throws StorageFullException {
        if (item == null) {
            throw new IllegalArgumentException("Cannot store a null food item.");
        }
        if (trays.isFull()) {
            throw StorageFullException.ofCapacity(CAPACITY);
        }
        trays.addFirst(item);
    }

    /**
     * Removes one tray, from the end the current mode dictates. O(1).
     *
     * @return the tray that was removed
     * @throws StorageEmptyException if the unit holds no trays
     */
    public FoodItem remove() throws StorageEmptyException {
        if (trays.isEmpty()) {
            throw StorageEmptyException.forOperation("remove a tray");
        }
        return mode == StorageMode.STACK_LIFO ? trays.removeFirst() : trays.removeLast();
    }

    /**
     * Looks at the tray on the front side (the top of the stack) without
     * removing it. O(1).
     *
     * @return the tray at the front
     * @throws StorageEmptyException if the unit holds no trays
     */
    public FoodItem peek() throws StorageEmptyException {
        if (trays.isEmpty()) {
            throw StorageEmptyException.forOperation("peek at the top tray");
        }
        return trays.peekFirst();
    }

    /**
     * Looks at the tray the next {@link #remove()} would take, which is the
     * front in Stack mode and the back in Queue mode. O(1).
     *
     * @return the tray that would leave next
     * @throws StorageEmptyException if the unit holds no trays
     */
    public FoodItem peekNextToRemove() throws StorageEmptyException {
        if (trays.isEmpty()) {
            throw StorageEmptyException.forOperation("peek at the next tray to remove");
        }
        return mode == StorageMode.STACK_LIFO ? trays.peekFirst() : trays.peekLast();
    }

    /**
     * Lists every tray from front to back. O(n).
     *
     * @return an unmodifiable list, front first
     * @throws StorageEmptyException if the unit holds no trays
     */
    public List<FoodItem> displayAll() throws StorageEmptyException {
        if (trays.isEmpty()) {
            throw StorageEmptyException.forOperation("display the trays");
        }
        return Collections.unmodifiableList(trays.toList());
    }

    /**
     * Switches the chef's working mode. No tray moves. O(1).
     *
     * @param mode the new mode, not null
     * @throws IllegalArgumentException if mode is null
     */
    public void setMode(StorageMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Storage mode cannot be null.");
        }
        this.mode = mode;
    }

    public StorageMode getMode() {
        return mode;
    }

    /** @return true when the unit holds no trays. O(1). */
    public boolean isEmpty() {
        return trays.isEmpty();
    }

    /** @return true when the unit holds its maximum of 8 trays. O(1). */
    public boolean isFull() {
        return trays.isFull();
    }

    /** @return how many trays are stored. O(1). */
    public int size() {
        return trays.size();
    }

    /** @return how many more trays would fit. O(1). */
    public int freeSpace() {
        return trays.remainingCapacity();
    }

    /**
     * @return the internal array index of the front tray, used by the display
     *         so the circular behaviour can be seen in the video
     */
    public int frontIndex() {
        return trays.frontIndex();
    }

    @Override
    public String toString() {
        return "FoodStorage[mode=" + mode + ", trays=" + trays.size() + "/" + CAPACITY + "]";
    }
}
