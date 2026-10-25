
package foodstorage.ui;

import foodstorage.model.FoodItem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end tests for the console layer.
 *
 * Instead of mocking anything, these tests type scripted lines into the
 * real StorageMenu and read back everything it printed.
 *
 * @author Grazielle Matsuyama
 */
class StorageMenuTest {

    /** A best-before date that is always valid: five days from today. */
    private static final String VALID_DATE =
            LocalDate.now().plusDays(5).format(FoodItem.DATE_FORMAT);

    /**
     * Runs the whole menu against a scripted conversation.
     *
     * @param lines the lines the user types, in order
     * @return everything the menu printed
     */
    private static String runMenu(String... lines) {
        String input = String.join(System.lineSeparator(), lines)
                + System.lineSeparator();

        ByteArrayOutputStream captured = new ByteArrayOutputStream();

        PrintStream out = new PrintStream(
                captured, true, StandardCharsets.UTF_8);

        try (Scanner scanner = new Scanner(input)) {
            new StorageMenu(scanner, out).run();
        }

        out.flush();
        return captured.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("1. Remove, peek and display on an empty unit report the error and keep running")
    void emptyStorageOperationsShowErrorAndKeepRunning() {
        String output = runMenu(
                "2",        // Queue mode
                "2",        // remove   -> empty
                "3",        // peek     -> empty
                "4",        // display  -> empty
                "0", "y");  // exit

        assertAll(
                () -> assertTrue(output.contains("Storage is EMPTY"),
                        "the user must be told the unit is empty"),
                () -> assertTrue(output.contains("cannot remove a tray")),
                () -> assertTrue(output.contains("cannot peek at the top tray")),
                () -> assertTrue(output.contains("cannot display the trays")),
                () -> assertTrue(output.contains("Application terminated"),
                        "the application survived every error and exited normally"));
    }

    @Test
    @DisplayName("2. A menu choice that is out of range or not a number is re-asked")
    void invalidMenuChoiceIsRejected() {
        String output = runMenu(
                "2",        // Queue mode
                "42",       // out of range
                "x",        // not a number
                "-1",       // out of range
                "0", "y");  // exit

        assertAll(
                () -> assertTrue(output.contains("42 is out of range")),
                () -> assertTrue(output.contains("between 0 and 6")),
                () -> assertTrue(output.contains("\"x\" is not a whole number")),
                () -> assertTrue(output.contains("-1 is out of range")),
                () -> assertTrue(output.contains("Application terminated")));
    }

    @Test
    @DisplayName("3. Bad food name, weight and date are each re-asked until valid")
    void invalidFoodWeightAndDateAreReAsked() {
        String tooFarAhead = LocalDate.now().plusDays(20).format(FoodItem.DATE_FORMAT);

        String output = runMenu(
                "2",                // Queue mode
                "1",                // add a tray
                "cake",             // not a food we store
                "9",                // not a valid number for the food list
                "pizza",            // valid
                "abc",              // not a number
                "-5",               // below the minimum
                "99999",            // above the maximum
                "350",              // valid
                "31/02/2026",       // not a real calendar date
                "01/01/2020",       // in the past
                tooFarAhead,        // more than 14 days ahead
                VALID_DATE,         // valid
                "4",                // display all
                "0", "y");          // exit

        assertAll(
                () -> assertTrue(output.contains("\"cake\" is not a food we store")),
                () -> assertTrue(output.contains("\"9\" is not a food we store")),
                () -> assertTrue(output.contains("\"abc\" is not a valid number")),
                () -> assertTrue(output.contains("-5 is out of range")),
                () -> assertTrue(output.contains("99999 is out of range")),
                () -> assertTrue(output.contains("\"31/02/2026\" is not a valid date"),
                        "strict parsing must reject the 31st of February"),
                () -> assertTrue(output.contains("cannot be in the past")),
                () -> assertTrue(output.contains("is too far ahead")),
                () -> assertTrue(output.contains("Added at the FRONT")),
                () -> assertTrue(output.contains("Pizza")),
                () -> assertTrue(output.contains("1/8 trays"),
                        "the tray count should be shown after the add"));
    }

}
