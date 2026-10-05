package foodstorage.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * One tray of food inside the storage unit.
 *
 * <p>Every field is {@code private final}: once a tray exists it cannot be
 * changed, only read through its getters (encapsulation and immutability).
 * The constructor validates all three arguments, so an invalid tray can never
 * be created - even if a future caller forgets to validate first. This is the
 * second line of defence behind {@code InputHelper}.</p>
 *
 * @author Member 1
 */
public final class FoodItem {

    /** Lightest tray the unit accepts, in grams. */
    public static final double MIN_WEIGHT_GRAMS = 1.0;

    /** Heaviest tray the unit accepts, in grams. */
    public static final double MAX_WEIGHT_GRAMS = 5000.0;

    /** The brief allows a best-before date up to two weeks ahead. */
    public static final int MAX_DAYS_AHEAD = 14;

    /** Date format used for both input and output: 04/10/2026. */
    public static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu");

    private static final DateTimeFormatter TIME_ADDED_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");

    private final FoodType type;
    private final double weightGrams;
    private final LocalDate bestBefore;
    private final LocalDateTime timeAdded;

    /**
     * Creates a validated tray and stamps it with the current time.
     *
     * @param type        one of the five foods, not null
     * @param weightGrams weight in grams, between 1 and 5000 inclusive
     * @param bestBefore  best-before date, today up to 14 days ahead
     * @throws IllegalArgumentException if any argument breaks a rule
     */
    public FoodItem(FoodType type, double weightGrams, LocalDate bestBefore) {
        if (type == null) {
            throw new IllegalArgumentException("Food type cannot be null.");
        }
        if (Double.isNaN(weightGrams) || weightGrams < MIN_WEIGHT_GRAMS
                || weightGrams > MAX_WEIGHT_GRAMS) {
            throw new IllegalArgumentException("Weight must be between "
                    + (int) MIN_WEIGHT_GRAMS + " and " + (int) MAX_WEIGHT_GRAMS
                    + " grams, but was " + weightGrams + ".");
        }
        if (bestBefore == null) {
            throw new IllegalArgumentException("Best-before date cannot be null.");
        }
        LocalDate today = LocalDate.now();
        if (bestBefore.isBefore(today)) {
            throw new IllegalArgumentException("Best-before date cannot be in the past: "
                    + bestBefore.format(DATE_FORMAT) + ".");
        }
        if (bestBefore.isAfter(today.plusDays(MAX_DAYS_AHEAD))) {
            throw new IllegalArgumentException("Best-before date must be at most "
                    + MAX_DAYS_AHEAD + " days from today, but was "
                    + bestBefore.format(DATE_FORMAT) + ".");
        }

        this.type = type;
        this.weightGrams = weightGrams;
        this.bestBefore = bestBefore;
        this.timeAdded = LocalDateTime.now();
    }

    public FoodType getType() {
        return type;
    }

    public double getWeightGrams() {
        return weightGrams;
    }

    public LocalDate getBestBefore() {
        return bestBefore;
    }

    public LocalDateTime getTimeAdded() {
        return timeAdded;
    }

    /**
     * @return whole days from today until the best-before date, 0 on the last day
     */
    public long getDaysUntilBestBefore() {
        return ChronoUnit.DAYS.between(LocalDate.now(), bestBefore);
    }

    /**
     * @return every detail of the tray on one line, used by the menu
     */
    public String toDisplayString() {
        return String.format("%-9s | %7.1f g | best before %s (%d day%s) | stored %s",
                type.getDisplayName(),
                weightGrams,
                bestBefore.format(DATE_FORMAT),
                getDaysUntilBestBefore(),
                getDaysUntilBestBefore() == 1 ? "" : "s",
                timeAdded.format(TIME_ADDED_FORMAT));
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}
