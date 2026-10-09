package foodstorage.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the validation built into {@link FoodItem}'s constructor, which is
 * the layer that makes an invalid tray impossible even when the caller forgets
 * to validate the input first.
 *
 * @author Member 1
 */
class FoodItemTest {

    @Test
    @DisplayName("1. A valid tray keeps all its fields and stamps the time it was added")
    void validItemKeepsAllFieldsAndSetsTimeAdded() {
        LocalDate bestBefore = LocalDate.now().plusDays(5);
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        FoodItem item = new FoodItem(FoodType.PIZZA, 350.0, bestBefore);

        assertAll(
                () -> assertEquals(FoodType.PIZZA, item.getType()),
                () -> assertEquals(350.0, item.getWeightGrams()),
                () -> assertEquals(bestBefore, item.getBestBefore()),
                () -> assertNotNull(item.getTimeAdded(), "the time added is set automatically"),
                () -> assertFalse(item.getTimeAdded().isBefore(before),
                        "the time added should be now, not in the past"),
                () -> assertEquals(5, item.getDaysUntilBestBefore()),
                () -> assertTrue(item.toDisplayString().contains("Pizza"),
                        "the display line should name the food"));
    }

    @Test
    @DisplayName("2. A null food type is refused")
    void nullFoodTypeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new FoodItem(null, 300.0, LocalDate.now().plusDays(3)));
    }

    @Test
    @DisplayName("3. A weight outside 1-5000 g is refused, the boundaries are accepted")
    void weightOutOfRangeThrows() {
        LocalDate bestBefore = LocalDate.now().plusDays(3);

        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new FoodItem(FoodType.FRIES, 0.0, bestBefore), "0 g is too light"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new FoodItem(FoodType.FRIES, -5.0, bestBefore),
                        "a negative weight is impossible"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new FoodItem(FoodType.FRIES, 99999.0, bestBefore),
                        "99999 g is heavier than the unit accepts"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new FoodItem(FoodType.FRIES, Double.NaN, bestBefore),
                        "NaN is not a weight"),
                () -> assertEquals(FoodItem.MIN_WEIGHT_GRAMS,
                        new FoodItem(FoodType.FRIES, FoodItem.MIN_WEIGHT_GRAMS, bestBefore)
                                .getWeightGrams(), "the lower boundary is valid"),
                () -> assertEquals(FoodItem.MAX_WEIGHT_GRAMS,
                        new FoodItem(FoodType.FRIES, FoodItem.MAX_WEIGHT_GRAMS, bestBefore)
                                .getWeightGrams(), "the upper boundary is valid"));
    }

    @Test
    @DisplayName("4. A null best-before date is refused")
    void nullBestBeforeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new FoodItem(FoodType.BURGER, 250.0, null));
    }

    @Test
    @DisplayName("5. A best-before date in the past is refused, today is accepted")
    void pastBestBeforeThrows() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new FoodItem(FoodType.BURGER, 250.0, LocalDate.now().minusDays(1)),
                        "yesterday is out of date"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new FoodItem(FoodType.BURGER, 250.0, LocalDate.now().minusYears(1))),
                () -> assertEquals(0,
                        new FoodItem(FoodType.BURGER, 250.0, LocalDate.now())
                                .getDaysUntilBestBefore(),
                        "food that is best before today is still usable today"));
    }

    @Test
    @DisplayName("6. A best-before date more than 14 days ahead is refused, day 14 is accepted")
    void bestBeforeTooFarAheadThrows() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new FoodItem(FoodType.HOT_DOG, 180.0,
                                LocalDate.now().plusDays(FoodItem.MAX_DAYS_AHEAD + 1)),
                        "15 days ahead breaks the two-week rule"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new FoodItem(FoodType.HOT_DOG, 180.0, LocalDate.now().plusDays(20))),
                () -> assertEquals(FoodItem.MAX_DAYS_AHEAD,
                        new FoodItem(FoodType.HOT_DOG, 180.0,
                                LocalDate.now().plusDays(FoodItem.MAX_DAYS_AHEAD))
                                .getDaysUntilBestBefore(),
                        "exactly 14 days ahead is the last valid date"),
                () -> assertEquals(14, FoodItem.MAX_DAYS_AHEAD,
                        "the brief allows a maximum of two weeks"));
    }
}
