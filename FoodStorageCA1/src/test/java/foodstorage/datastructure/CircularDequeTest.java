package foodstorage.datastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the data structure itself, independently of the food scenario.
 *
 * <p>The important one is {@link #wrapsAroundCorrectlyManyTimes()}, which
 * pushes far more elements through the ring than it has cells, so any mistake
 * in the modulo arithmetic would show up as wrong data or an exception.</p>
 *
 * @author Member 1
 */
class CircularDequeTest {

    private static final int CAPACITY = 8;

    private CircularDeque<String> deque;

    @BeforeEach
    void createEmptyDeque() {
        deque = new CircularDeque<>(CAPACITY);
    }

    @Test
    @DisplayName("1. A new deque is empty, not full, and reports its capacity")
    void newDequeIsEmptyAndNotFull() {
        assertAll(
                () -> assertTrue(deque.isEmpty(), "a new deque should be empty"),
                () -> assertFalse(deque.isFull(), "a new deque should not be full"),
                () -> assertEquals(0, deque.size()),
                () -> assertEquals(CAPACITY, deque.capacity()),
                () -> assertEquals(CAPACITY, deque.remainingCapacity()),
                () -> assertTrue(deque.toList().isEmpty()));
    }

    @Test
    @DisplayName("2. addFirst + removeFirst behaves like a stack (LIFO)")
    void addFirstThenRemoveFirstBehavesLikeAStack() {
        deque.addFirst("Burger");
        deque.addFirst("Pizza");
        deque.addFirst("Fries");

        assertAll(
                () -> assertEquals(3, deque.size()),
                () -> assertEquals("Fries", deque.peekFirst(), "newest is at the front"),
                () -> assertEquals("Fries", deque.removeFirst()),
                () -> assertEquals("Pizza", deque.removeFirst()),
                () -> assertEquals("Burger", deque.removeFirst()),
                () -> assertTrue(deque.isEmpty()));
    }

    @Test
    @DisplayName("3. addFirst + removeLast behaves like a queue (FIFO)")
    void addFirstThenRemoveLastBehavesLikeAQueue() {
        deque.addFirst("Burger");
        deque.addFirst("Pizza");
        deque.addFirst("Fries");

        assertAll(
                () -> assertEquals("Burger", deque.peekLast(), "oldest is at the back"),
                () -> assertEquals("Burger", deque.removeLast()),
                () -> assertEquals("Pizza", deque.removeLast()),
                () -> assertEquals("Fries", deque.removeLast()),
                () -> assertTrue(deque.isEmpty()));
    }

    @Test
    @DisplayName("4. The front index wraps around correctly over many passes")
    void wrapsAroundCorrectlyManyTimes() {
        // 100 elements through 8 cells: the ring is reused more than 12 times.
        for (int i = 0; i < 100; i++) {
            deque.addFirst("item-" + i);
            assertEquals("item-" + i, deque.removeLast(),
                    "FIFO order must survive the wrap-around at step " + i);
            assertTrue(deque.isEmpty(), "every element was removed at step " + i);
        }

        // Fill, wrap, and check the contents are still in the right order.
        for (int i = 0; i < CAPACITY; i++) {
            deque.addFirst("fill-" + i);
        }
        assertAll(
                () -> assertTrue(deque.isFull()),
                () -> assertEquals("fill-7", deque.peekFirst()),
                () -> assertEquals("fill-0", deque.peekLast()),
                () -> assertTrue(deque.frontIndex() >= 0 && deque.frontIndex() < CAPACITY,
                        "the front index must always stay inside the array"));
    }

    @Test
    @DisplayName("5. Adding beyond the capacity is refused at both ends")
    void addingBeyondCapacityThrows() {
        for (int i = 0; i < CAPACITY; i++) {
            deque.addFirst("item-" + i);
        }

        assertAll(
                () -> assertTrue(deque.isFull()),
                () -> assertEquals(0, deque.remainingCapacity()),
                () -> assertThrows(IllegalStateException.class, () -> deque.addFirst("overflow")),
                () -> assertThrows(IllegalStateException.class, () -> deque.addLast("overflow")),
                () -> assertEquals(CAPACITY, deque.size(), "a refused add must not change the size"));
    }

    @Test
    @DisplayName("6. Removing or peeking an empty deque throws, and null is refused")
    void removingOrPeekingWhenEmptyThrows() {
        assertAll(
                () -> assertThrows(NoSuchElementException.class, () -> deque.removeFirst()),
                () -> assertThrows(NoSuchElementException.class, () -> deque.removeLast()),
                () -> assertThrows(NoSuchElementException.class, () -> deque.peekFirst()),
                () -> assertThrows(NoSuchElementException.class, () -> deque.peekLast()),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> deque.get(0)),
                () -> assertThrows(IllegalArgumentException.class, () -> deque.addFirst(null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new CircularDeque<String>(0), "capacity must be positive"));
    }

    @Test
    @DisplayName("7. get(i) and toList() read from front to back")
    void getAndToListReturnFrontToBackOrder() {
        deque.addFirst("Burger");   // ends up at the back
        deque.addFirst("Pizza");
        deque.addFirst("Fries");    // ends up at the front

        List<String> all = deque.toList();

        assertAll(
                () -> assertEquals(List.of("Fries", "Pizza", "Burger"), all),
                () -> assertEquals("Fries", deque.get(0), "position 0 is the front"),
                () -> assertEquals("Pizza", deque.get(1)),
                () -> assertEquals("Burger", deque.get(2), "the last position is the back"),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> deque.get(3)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> deque.get(-1)),
                () -> assertEquals(3, deque.size(), "reading must not remove anything"));
    }
}
