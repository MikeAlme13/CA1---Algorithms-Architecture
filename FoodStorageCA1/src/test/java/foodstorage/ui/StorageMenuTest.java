
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

}
