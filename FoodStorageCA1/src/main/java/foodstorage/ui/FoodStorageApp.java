package foodstorage.ui;

import java.util.Scanner;
/**
 * Entry point of the application.
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
    }
}