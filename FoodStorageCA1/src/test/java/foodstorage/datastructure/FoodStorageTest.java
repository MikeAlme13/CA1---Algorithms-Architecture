package foodstorage.datastructure;

import foodstorage.exception.StorageEmptyException;
import foodstorage.exception.StorageFullException;
import foodstorage.model.FoodItem;
import foodstorage.model.FoodType;
import foodstorage.model.StorageMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the storage unit: the chef's two modes, the 8-tray limit, the
 * empty-storage errors and the three searches.
 *
 * @author Member 1
 */
class FoodStorageTest {

    private FoodStorage storage;

    @BeforeEach
    void createEmptyStorage() {
        storage = new FoodStorage(StorageMode.QUEUE_FIFO);
    }

    /**
     * @param type       which food
     * @param weight     grams
     * @param daysAhead  best-before, counted from today
     * @return a valid tray for use in the tests
     */
    private static FoodItem tray(FoodType type, double weight, int daysAhead) {
        return new FoodItem(type, weight, LocalDate.now().plusDays(daysAhead));
    }

    @Test
    @DisplayName("1. A new unit is empty, not full, and has 8 free spaces")
    void newStorageIsEmptyAndNotFull() {
        assertAll(
                () -> assertTrue(storage.isEmpty()),
                () -> assertFalse(storage.isFull()),
                () -> assertEquals(0, storage.size()),
                () -> assertEquals(FoodStorage.CAPACITY, storage.freeSpace()),
                () -> assertEquals(8, FoodStorage.CAPACITY, "the brief fixes the capacity at 8"),
                () -> assertEquals(StorageMode.QUEUE_FIFO, storage.getMode()));
    }

    @Test
    @DisplayName("2. add() always places the tray at the front, and peek() returns it")
    void addPlacesItemAtFrontAndPeekReturnsIt() throws Exception {
        FoodItem burger = tray(FoodType.BURGER, 200, 3);
        FoodItem pizza = tray(FoodType.PIZZA, 350, 5);

        storage.add(burger);
        storage.add(pizza);

        assertAll(
                () -> assertSame(pizza, storage.peek(), "the newest tray is at the front"),
                () -> assertEquals(2, storage.size()),
                () -> assertEquals(6, storage.freeSpace()),
                () -> assertEquals(List.of(pizza, burger), storage.displayAll(),
                        "displayAll lists front to back"),
                () -> assertThrows(IllegalArgumentException.class, () -> storage.add(null)));
    }

    @Test
    @DisplayName("3. Stack mode removes the newest tray, from the front")
    void stackModeRemovesNewestFirst() throws Exception {
        FoodItem burger = tray(FoodType.BURGER, 200, 3);
        FoodItem pizza = tray(FoodType.PIZZA, 350, 5);
        FoodItem fries = tray(FoodType.FRIES, 150, 7);
        storage.add(burger);
        storage.add(pizza);
        storage.add(fries);
        storage.setMode(StorageMode.STACK_LIFO);

        assertAll(
                () -> assertSame(fries, storage.peekNextToRemove()),
                () -> assertSame(fries, storage.remove()),
                () -> assertSame(pizza, storage.remove()),
                () -> assertSame(burger, storage.remove()),
                () -> assertTrue(storage.isEmpty()));
    }

    @Test
    @DisplayName("4. Queue mode removes the oldest tray, from the back")
    void queueModeRemovesOldestFirst() throws Exception {
        FoodItem burger = tray(FoodType.BURGER, 200, 3);
        FoodItem pizza = tray(FoodType.PIZZA, 350, 5);
        FoodItem fries = tray(FoodType.FRIES, 150, 7);
        storage.add(burger);
        storage.add(pizza);
        storage.add(fries);

        assertAll(
                () -> assertSame(burger, storage.peekNextToRemove()),
                () -> assertSame(fries, storage.peek(), "peek still shows the front"),
                () -> assertSame(burger, storage.remove()),
                () -> assertSame(pizza, storage.remove()),
                () -> assertSame(fries, storage.remove()),
                () -> assertTrue(storage.isEmpty()));
    }

    @Test
    @DisplayName("5. Changing mode moves no tray, it only changes the removal end")
    void changingModeDoesNotReorderItems() throws Exception {
        FoodItem burger = tray(FoodType.BURGER, 200, 3);
        FoodItem pizza = tray(FoodType.PIZZA, 350, 5);
        FoodItem fries = tray(FoodType.FRIES, 150, 7);
        storage.add(burger);
        storage.add(pizza);
        storage.add(fries);

        List<FoodItem> beforeSwitch = storage.displayAll();
        storage.setMode(StorageMode.STACK_LIFO);
        List<FoodItem> afterSwitch = storage.displayAll();

        assertAll(
                () -> assertEquals(beforeSwitch, afterSwitch, "the order must be untouched"),
                () -> assertEquals(List.of(fries, pizza, burger), afterSwitch),
                () -> assertEquals(3, storage.size()),
                () -> assertSame(fries, storage.remove(), "stack mode now takes the front"),
                () -> assertThrows(IllegalArgumentException.class, () -> storage.setMode(null)));
    }

    @Test
    @DisplayName("6. The 9th tray is refused with StorageFullException")
    void ninthItemThrowsStorageFullException() throws Exception {
        for (int i = 0; i < FoodStorage.CAPACITY; i++) {
            storage.add(tray(FoodType.BURGER, 100 + i, 2));
        }

        StorageFullException thrown = assertThrows(StorageFullException.class,
                () -> storage.add(tray(FoodType.PIZZA, 400, 4)));

        assertAll(
                () -> assertTrue(storage.isFull()),
                () -> assertEquals(FoodStorage.CAPACITY, storage.size(),
                        "a refused add must not change the contents"),
                () -> assertEquals(0, storage.freeSpace()),
                () -> assertTrue(thrown.getMessage().contains("FULL"),
                        "the message should tell the user the unit is full"));
    }

    @Test
    @DisplayName("7. remove() on an empty unit throws StorageEmptyException")
    void removeFromEmptyThrowsStorageEmptyException() {
        StorageEmptyException thrown = assertThrows(StorageEmptyException.class,
                () -> storage.remove());

        assertTrue(thrown.getMessage().contains("EMPTY"),
                "the message should tell the user the unit is empty");
    }

    @Test
    @DisplayName("8. peek() and peekNextToRemove() on an empty unit throw")
    void peekOnEmptyThrowsStorageEmptyException() {
        assertAll(
                () -> assertThrows(StorageEmptyException.class, () -> storage.peek()),
                () -> assertThrows(StorageEmptyException.class, () -> storage.peekNextToRemove()));
    }

    @Test
    @DisplayName("9. displayAll() on an empty unit throws StorageEmptyException")
    void displayAllOnEmptyThrows() {
        StorageEmptyException thrown = assertThrows(StorageEmptyException.class,
                () -> storage.displayAll());

        assertTrue(thrown.getMessage().contains("EMPTY"),
                "the message should tell the user the unit is empty");
    }
}
