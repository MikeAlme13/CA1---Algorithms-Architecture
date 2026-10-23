
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
    /**
     * Reads the weight of a new tray.
     */
    public double readWeight() {
        return readDoubleInRange("  Weight in grams ("
                        + (int) FoodItem.MIN_WEIGHT_GRAMS + "-"
                        + (int) FoodItem.MAX_WEIGHT_GRAMS + "): ",
                FoodItem.MIN_WEIGHT_GRAMS, FoodItem.MAX_WEIGHT_GRAMS);
    }

    /**
     * Reads a decimal number inside a range.
     */
    public double readDoubleInRange(String prompt, double min, double max) {
        while (true) {
            out.print(prompt);
            String line = nextLine();
            try {
                double value = Double.parseDouble(line.trim().replace(',', '.'));
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    out.println("  >> That is not a valid number.");
                    continue;
                }
                if (value < min || value > max) {
                    out.println("  >> " + trim(value) + " is out of range. Enter a value between "
                            + trim(min) + " and " + trim(max) + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                out.println("  >> \"" + line.trim() + "\" is not a valid number. Enter a value between "
                        + trim(min) + " and " + trim(max) + ".");
            }
        }
    }

    /**
     * Formats numbers without unnecessary trailing .0.
     */
    private static String trim(double value) {
        if (value == Math.rint(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    /**
     * Reads the best-before date of a new tray.
     */
    public LocalDate readBestBefore() {
        LocalDate today = LocalDate.now();
        LocalDate latest = today.plusDays(FoodItem.MAX_DAYS_AHEAD);
        while (true) {
            out.println("  Best before must be between " + today.format(FoodItem.DATE_FORMAT)
                    + " and " + latest.format(FoodItem.DATE_FORMAT)
                    + " (today up to " + FoodItem.MAX_DAYS_AHEAD + " days ahead).");
            LocalDate date = readDate("  Best-before date (dd/MM/yyyy): ");
            if (date.isBefore(today)) {
                out.println("  >> " + date.format(FoodItem.DATE_FORMAT)
                        + " cannot be in the past. The food would already be out of date.");
                continue;
            }
            if (date.isAfter(latest)) {
                out.println("  >> " + date.format(FoodItem.DATE_FORMAT)
                        + " is too far ahead. The unit accepts at most "
                        + FoodItem.MAX_DAYS_AHEAD + " days from today.");
                continue;
            }
            return date;
        }
    }

    /**
     * Reads any calendar date with strict parsing.
     */
    public LocalDate readDate(String prompt) {
        while (true) {
            out.print(prompt);
            String line = nextLine().trim();
            try {
                return LocalDate.parse(line, STRICT_DATE);
            } catch (DateTimeException e) {
                out.println("  >> \"" + line + "\" is not a valid date. "
                        + "Use the format dd/MM/yyyy, for example "
                        + LocalDate.now().plusDays(5).format(FoodItem.DATE_FORMAT) + ".");
            }
        }
    }

}
