
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
   
    /**
     * Asks for the working mode, then loops the menu until the user confirms
     * an exit.
     */
    public void run() {
        printWelcome();
        storage.setMode(input.readStorageMode());
        out.println();
        out.println("Mode set to " + storage.getMode().getDisplayName()
                + " - " + storage.getMode().getDescription() + ".");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = input.readIntInRange("Your choice (0-6): ", 0, 6);
            out.println();
            switch (choice) {
                case 1 -> addFood();
                case 2 -> removeFood();
                case 3 -> peekTop();
                case 4 -> displayAll();
                case 5 -> changeMode();
                case 6 -> printComplexityReport();
                case 0 -> running = !confirmExit();
                default -> out.println("Unknown option.");
            }
            out.println();
        }
    }

    private void printWelcome() {
        out.println(LINE);
        out.println("     PARTY FOOD STORAGE UNIT - CA1 Algorithms & Data Structures");
        out.println(LINE);
        out.println("The unit holds at most " + FoodStorage.CAPACITY
                + " trays of food in a circular-array deque.");
        out.println();
    }

    private void printMenu() {
        out.println(LINE);
        out.printf("  Mode: %-13s   Trays: %d/%d   Free space: %d%n",
                storage.getMode().getDisplayName(),
                storage.size(), FoodStorage.CAPACITY, storage.freeSpace());
        out.println(LINE);
        out.println("  1. Add a tray of food");
        out.println("  2. Remove a tray (" + removalEndDescription() + ")");
        out.println("  3. Peek at the top tray (front side)");
        out.println("  4. Display all trays");
        out.println("  5. Change mode (Stack / Queue)");
        out.println("  6. Time complexity report");
        out.println("  0. Exit");
        out.println(THIN_LINE);
    }

    private String removalEndDescription() {
        return storage.getMode() == StorageMode.STACK_LIFO
                ? "from the FRONT, newest first"
                : "from the BACK, oldest first";
    }


    // ---------------------------------------------------------------- option 1

    private void addFood() {
        out.println("ADD A TRAY");
        if (storage.isFull()) {
            // Checked before prompting, so the user is not asked for details
            // that are about to be thrown away.
            out.println("  >> " + StorageFullException.ofCapacity(FoodStorage.CAPACITY)
                    .getMessage());
            return;
        }

        FoodType type = input.readFoodType();
        double weight = input.readWeight();
        LocalDate bestBefore = input.readBestBefore();

        try {
            FoodItem item = new FoodItem(type, weight, bestBefore);
            storage.add(item);
            out.println();
            out.println("  Added at the FRONT: " + item.toDisplayString());
            out.println("  The unit now holds " + storage.size() + "/"
                    + FoodStorage.CAPACITY + " trays.");
        } catch (StorageFullException e) {
            out.println("  >> " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Defence in depth: InputHelper should already have stopped this.
            out.println("  >> Invalid tray rejected: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- option 2

    private void removeFood() {
        out.println("REMOVE A TRAY (" + storage.getMode().getDisplayName() + ")");
        try {
            FoodItem removed = storage.remove();
            out.println("  Removed " + removalEndDescription().toUpperCase() + ": "
                    + removed.toDisplayString());
            out.println("  The unit now holds " + storage.size() + "/"
                    + FoodStorage.CAPACITY + " trays.");
        } catch (StorageEmptyException e) {
            out.println("  >> " + e.getMessage());
        }
    }

}
