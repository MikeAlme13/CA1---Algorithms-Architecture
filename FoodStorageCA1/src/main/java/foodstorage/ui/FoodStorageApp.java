package foodstorage.ui;

import java.util.Scanner;
/**
 * Entry point of the application.
 *
 * * <p>Its only jobs are to open a {@link Scanner} on standard input, hand it to
 * the {@link StorageMenu}, and close it again when the menu ends. Keeping
 * {@code main} this small is what allows the menu to be unit-tested with
 * scripted input instead of a keyboard.</p>
 *
 * <p>Run it in NetBeans with Right-click the project, then Run. If NetBeans
 * asks for a main class, choose {@code foodstorage.ui.FoodStorageApp}.</p>
 *
 * @author Grazielle Matsuyama
 */
public final class FoodStorageApp {

    private FoodStorageApp() {
        // Utility class: never instantiated.
    }
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new StorageMenu(scanner, System.out).run();
        } 
        catch (IllegalStateException e) {
            // Raised by InputHelper when standard input ends unexpectedly,
            // for example if the application is piped a file that runs out.
            System.out.println();
            System.out.println("Input ended unexpectedly - the application has stopped.");
        }
    }
}