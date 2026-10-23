
package foodstorage.ui;

import foodstorage.datastructure.FoodStorage;
import foodstorage.exception.StorageEmptyException;
import foodstorage.exception.StorageFullException;
import foodstorage.model.FoodItem;
import foodstorage.model.FoodType;
import foodstorage.model.StorageMode;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * The console menu: the only class the user interacts with.
 *
 * @author Grazielle Matsuyama
 */
public class StorageMenu {

    private static final String LINE =
            "============================================================";
    private static final String THIN_LINE =
            "------------------------------------------------------------";

    private final FoodStorage storage;
    private final InputHelper input;
    private final PrintStream out;

    /**
     * Builds a menu over a new, empty storage unit.
     *
     * @param scanner source of user input, not null
     * @param out where all output is printed, not null
     */
    public StorageMenu(Scanner scanner, PrintStream out) {
        this.out = out;
        this.input = new InputHelper(scanner, out);
        this.storage = new FoodStorage(StorageMode.QUEUE_FIFO);
    }

    /** Convenience constructor for the real application. */
    public StorageMenu(Scanner scanner) {
        this(scanner, System.out);
    }
}
