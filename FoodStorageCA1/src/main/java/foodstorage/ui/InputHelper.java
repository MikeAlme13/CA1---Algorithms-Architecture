
package foodstorage.ui;

import foodstorage.model.FoodItem;
import foodstorage.model.FoodType;
import foodstorage.model.StorageMode;

import java.io.PrintStream;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Scanner;

/**
 * Reads and validates everything the user types.
 *
 * @author Grazielle Matsuyama
 */
public class InputHelper {

    private static final DateTimeFormatter STRICT_DATE =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    private final Scanner scanner;
    private final PrintStream out;

    public InputHelper(Scanner scanner, PrintStream out) {
        if (scanner == null || out == null) {
            throw new IllegalArgumentException(
                    "Scanner and output stream are required.");
        }
        this.scanner = scanner;
        this.out = out;
    }

    public InputHelper(Scanner scanner) {
        this(scanner, System.out);
    }
}
