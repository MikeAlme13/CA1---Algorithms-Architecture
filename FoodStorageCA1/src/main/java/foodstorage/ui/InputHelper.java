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
 * <p>Every read method loops until the input is valid, printing an
 * explanation each time it is not, so bad input can never reach the model or
 * crash the application. This is the first of the two validation layers; the
 * second is {@link FoodItem}'s constructor, which checks the same rules again
 * so an invalid tray cannot exist even if a caller skips this class.</p>
 *
 * <p>The {@link Scanner} and {@link PrintStream} are injected rather than
 * hard-coded to {@code System.in} / {@code System.out}, which is what lets
 * {@code StorageMenuTest} feed scripted input into the real menu and read back
 * the messages a user would see.</p>
 *
 * @author Grazielle Matsuyama
 */
public class InputHelper {

    /**
     * Strict date parsing: {@code uuuu} (proleptic year) rather than
     * {@code yyyy} (year-of-era) is required, because
     * {@link ResolverStyle#STRICT} needs an era-independent year field. Strict
     * mode is what makes 31/02/2026 an error instead of being quietly
     * resolved to 28/02/2026.
     */
    private static final DateTimeFormatter STRICT_DATE =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final Scanner scanner;
    private final PrintStream out;

    /**
     * @param scanner source of user input, not null
     * @param out     where prompts and error messages are printed, not null
     */
    public InputHelper(Scanner scanner, PrintStream out) {
        if (scanner == null || out == null) {
            throw new IllegalArgumentException("Scanner and output stream are required.");
        }
        this.scanner = scanner;
        this.out = out;
    }

    /** Convenience constructor for the real application. */
    public InputHelper(Scanner scanner) {
        this(scanner, System.out);
    }

    /**
     * Reads a whole number inside a range, re-asking until it is valid.
     *
     * @param prompt text shown before the cursor
     * @param min    lowest accepted value, inclusive
     * @param max    highest accepted value, inclusive
     * @return a valid int between min and max
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
     * Reads one of the five foods by name or by number, re-asking until the
     * text matches a food the unit stores.
     *
     * @return the chosen food type, never null
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
     * Reads the weight of a new tray, re-asking until it is a number inside
     * the accepted range.
     *
     * @return a valid weight in grams
     */
    public double readWeight() {
        return readDoubleInRange("  Weight in grams ("
                        + (int) FoodItem.MIN_WEIGHT_GRAMS + "-"
                        + (int) FoodItem.MAX_WEIGHT_GRAMS + "): ",
                FoodItem.MIN_WEIGHT_GRAMS, FoodItem.MAX_WEIGHT_GRAMS);
    }

    /**
     * Reads a decimal number inside a range, re-asking until it is valid.
     *
     * @param prompt text shown before the cursor
     * @param min    lowest accepted value, inclusive
     * @param max    highest accepted value, inclusive
     * @return a valid value between min and max
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
     * Reads the best-before date of a new tray. Applies all three rules from
     * the brief: a real calendar date, not in the past, and at most 14 days
     * ahead.
     *
     * @return a valid best-before date
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
     * Reads any calendar date with strict parsing, used for the search range
     * where past and far-future dates are legitimate.
     *
     * @param prompt text shown before the cursor
     * @return a valid date
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

    /**
     * Reads a yes/no answer, re-asking until it is one of them.
     *
     * @param prompt text shown before the cursor
     * @return true for yes, false for no
     */
    public boolean readYesNo(String prompt) {
        while (true) {
            out.print(prompt);
            String answer = nextLine().trim().toLowerCase();
            switch (answer) {
                case "y":
                case "yes":
                    return true;
                case "n":
                case "no":
                    return false;
                default:
                    out.println("  >> Please answer y (yes) or n (no).");
            }
        }
    }

    /**
     * Asks the chef which of the two working modes to use.
     *
     * @return the chosen mode, never null
     */
    public StorageMode readStorageMode() {
        out.println("How will the chef work with the unit?");
        out.println("  1. " + StorageMode.STACK_LIFO.getDisplayName()
                + " - add and remove on the front side ("
                + StorageMode.STACK_LIFO.getDescription() + ")");
        out.println("  2. " + StorageMode.QUEUE_FIFO.getDisplayName()
                + " - add at the front, remove from the back ("
                + StorageMode.QUEUE_FIFO.getDescription() + ")  [recommended]");
        int choice = readIntInRange("Mode (1-2): ", 1, 2);
        return choice == 1 ? StorageMode.STACK_LIFO : StorageMode.QUEUE_FIFO;
    }

    /**
     * Reads the next line, or fails loudly if the input stream has ended.
     *
     * @return the raw line typed by the user
     * @throws IllegalStateException when there is no more input to read
     */
    private String nextLine() {
        if (!scanner.hasNextLine()) {
            throw new IllegalStateException("Input ended while the application was "
                    + "still waiting for an answer.");
        }
        return scanner.nextLine();
    }

    /**
     * @param value a number to show in a message
     * @return the number without a trailing ".0", so 500.0 prints as "500"
     */
    private static String trim(double value) {
        if (value == Math.rint(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}
