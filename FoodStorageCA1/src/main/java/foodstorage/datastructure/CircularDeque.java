package foodstorage.datastructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A fixed-capacity double-ended queue (deque) built on a circular array.
 *
 * <h2>Why this structure</h2>
 * <p>The storage unit physically has two sides and a fixed capacity. A deque
 * has two ends, so it is a stack and a queue at the same time, and a circular
 * array gives every operation O(1) with no element shifting at all.</p>
 *
 * <h2>How the circle works</h2>
 * <p>Only two numbers are stored: {@code front} (the array index of the first
 * element) and {@code size}. The back index is calculated, never stored. The
 * modulo operator makes index 0 follow index capacity-1, so the array behaves
 * like a ring:</p>
 *
 * <pre>
 *   addFirst:     front = (front - 1 + capacity) % capacity
 *   removeFirst:  front = (front + 1) % capacity
 *   back:         (front + size - 1) % capacity
 *   addLast:      (front + size) % capacity
 * </pre>
 *
 * <p>The {@code + capacity} in addFirst stops the index going negative: from
 * index 0 the step left is to the last cell of the array, not to -1.</p>
 *
 * <h2>Complexity</h2>
 * <p>addFirst, addLast, removeFirst, removeLast, peekFirst, peekLast, get,
 * size, isEmpty and isFull are all O(1) time and O(1) extra space. Only
 * {@link #toList()} is O(n), because it has to visit every element.</p>
 *
 * <p>The class is generic so it is reusable with any type; this project uses
 * CircularDeque of FoodItem. It deliberately throws only standard exceptions -
 * the food-specific StorageFullException and StorageEmptyException belong to
 * {@link FoodStorage}, one layer up, which keeps the data structure
 * independent of the scenario.</p>
 *
 * @param <T> type of element stored
 * @author Member 1
 */
public class CircularDeque<T> {

    /** Backing array. Object[] because Java cannot create a generic array. */
    private final Object[] data;

    /** Fixed number of cells; never changes after construction. */
    private final int capacity;

    /** Array index of the element at the front. */
    private int front;

    /** How many cells are in use. The back index is derived from this. */
    private int size;

    /**
     * @param capacity number of elements the deque can hold, must be positive
     * @throws IllegalArgumentException if capacity is zero or negative
     */
    public CircularDeque(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive, but was "
                    + capacity + ".");
        }
        this.capacity = capacity;
        this.data = new Object[capacity];
        this.front = 0;
        this.size = 0;
    }

    /**
     * Inserts at the front. O(1).
     *
     * @param element the element to add, not null
     * @throws IllegalStateException    if the deque is full
     * @throws IllegalArgumentException if element is null
     */
    public void addFirst(T element) {
        requireNotNull(element);
        requireNotFull();
        front = (front - 1 + capacity) % capacity;
        data[front] = element;
        size++;
    }

    /**
     * Inserts at the back. O(1).
     *
     * @param element the element to add, not null
     * @throws IllegalStateException    if the deque is full
     * @throws IllegalArgumentException if element is null
     */
    public void addLast(T element) {
        requireNotNull(element);
        requireNotFull();
        data[(front + size) % capacity] = element;
        size++;
    }

    /**
     * Removes and returns the front element. O(1).
     *
     * @return the element that was at the front
     * @throws NoSuchElementException if the deque is empty
     */
    public T removeFirst() {
        requireNotEmpty();
        T element = elementAt(front);
        data[front] = null;                 // let the object be garbage-collected
        front = (front + 1) % capacity;
        size--;
        return element;
    }

    /**
     * Removes and returns the back element. O(1).
     *
     * @return the element that was at the back
     * @throws NoSuchElementException if the deque is empty
     */
    public T removeLast() {
        requireNotEmpty();
        int back = backIndex();
        T element = elementAt(back);
        data[back] = null;                  // let the object be garbage-collected
        size--;
        return element;
    }

    /**
     * @return the front element without removing it. O(1).
     * @throws NoSuchElementException if the deque is empty
     */
    public T peekFirst() {
        requireNotEmpty();
        return elementAt(front);
    }

    /**
     * @return the back element without removing it. O(1).
     * @throws NoSuchElementException if the deque is empty
     */
    public T peekLast() {
        requireNotEmpty();
        return elementAt(backIndex());
    }

    /**
     * Reads by logical position, where 0 is the front. O(1).
     *
     * @param index 0-based position from the front
     * @return the element at that position
     * @throws IndexOutOfBoundsException if index is outside 0..size-1
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index
                    + " is outside 0.." + (size - 1) + " (size " + size + ").");
        }
        return elementAt((front + index) % capacity);
    }

    /**
     * Copies the elements into a list, front first. O(n) time and O(n) space.
     *
     * @return a new list from front to back; empty when the deque is empty
     */
    public List<T> toList() {
        List<T> copy = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            copy.add(elementAt((front + i) % capacity));
        }
        return copy;
    }

    /** @return true when no element is stored. O(1). */
    public boolean isEmpty() {
        return size == 0;
    }

    /** @return true when every cell is in use. O(1). */
    public boolean isFull() {
        return size == capacity;
    }

    /** @return how many elements are stored. O(1). */
    public int size() {
        return size;
    }

    /** @return the fixed capacity given to the constructor. O(1). */
    public int capacity() {
        return capacity;
    }

    /** @return free cells remaining. O(1). */
    public int remainingCapacity() {
        return capacity - size;
    }

    /** Removes every element and resets the indexes. O(n). */
    public void clear() {
        Arrays.fill(data, null);
        front = 0;
        size = 0;
    }

    /**
     * Exposes the internal index of the front, for teaching and for the unit
     * tests that prove the wrap-around arithmetic.
     *
     * @return the array index currently holding the front element
     */
    public int frontIndex() {
        return front;
    }

    /**
     * @return the array index currently holding the back element
     * @throws NoSuchElementException if the deque is empty
     */
    public int backIndexForDisplay() {
        requireNotEmpty();
        return backIndex();
    }

    private int backIndex() {
        return (front + size - 1) % capacity;
    }

    @SuppressWarnings("unchecked")
    private T elementAt(int arrayIndex) {
        return (T) data[arrayIndex];
    }

    private void requireNotNull(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot store null in the deque.");
        }
    }

    private void requireNotFull() {
        if (isFull()) {
            throw new IllegalStateException("Deque is full (capacity " + capacity + ").");
        }
    }

    private void requireNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque is empty.");
        }
    }

    @Override
    public String toString() {
        return "CircularDeque[front=" + front + ", size=" + size
                + ", capacity=" + capacity + ", elements=" + toList() + "]";
    }
}
