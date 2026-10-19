
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

    /**
     * Reads a whole number inside a range, re-asking until it is valid.
     */
    public int readIntInRange(String prompt, int min, int max) {
        while (true) {
            out.print(prompt);
            String line = nextLine();
            try {
                int value = Integer.parseInt(line.trim());
                if (value < min || value > max) {
                    out.println("  >> " + value + " is out of range. Enter a number between "
                            + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                out.println("  >> \"" + line.trim() + "\" is not a whole number. Enter a number between "
                        + min + " and " + max + ".");
            }
        }
    }

    /**
     * Reads one of the five foods by name or by number.
     */
    public FoodType readFoodType() {
        while (true) {
            out.println("  Foods: " + FoodType.menuList());
            out.print("  Food name or number: ");
            String line = nextLine();
            FoodType type = FoodType.fromInput(line);
            if (type != null) {
                return type;
            }
            out.println("  >> \"" + line.trim() + "\" is not a food we store. "
                    + "Choose one of the five above.");
        }
    }

    /**
     * Reads the next line, or fails if the input stream has ended.
     */
    private String nextLine() {
        if (!scanner.hasNextLine()) {
            throw new IllegalStateException("Input ended while the application was "
                    + "still waiting for an answer.");
        }
        return scanner.nextLine();
    }

}
