package foodstorage.model;

/**
 * The five party foods the storage unit accepts.
 *
 * <p>Using an enum instead of a String makes an invalid food type impossible:
 * the compiler only allows these five constants, so no validation is ever
 * needed once a {@code FoodType} exists.</p>
 *
 * @author Member 1
 */
public enum FoodType {

    BURGER("Burger"),
    PIZZA("Pizza"),
    FRIES("Fries"),
    SANDWICH("Sandwich"),
    HOT_DOG("Hot Dog");

    private final String displayName;

    FoodType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the food name as it should be shown to the user, e.g. "Hot Dog"
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Converts whatever the user typed into a {@code FoodType}.
     *
     * <p>Accepts the menu number (1-5), the name in any capitalisation, and
     * the common variations of "hot dog" (hotdog, hot-dog). Returns
     * {@code null} when the text matches no food, which lets
     * {@code InputHelper} print a message and ask again.</p>
     *
     * @param input raw text typed by the user, may be null
     * @return the matching food type, or null when there is no match
     */
    public static FoodType fromInput(String input) {
        if (input == null) {
            return null;
        }
        String cleaned = input.trim().toLowerCase()
                .replace('-', ' ')
                .replace('_', ' ')
                .replaceAll("\s+", " ");

        switch (cleaned) {
            case "1":
            case "burger":
            case "burgers":
                return BURGER;
            case "2":
            case "pizza":
            case "pizzas":
                return PIZZA;
            case "3":
            case "fries":
            case "fry":
            case "chips":
                return FRIES;
            case "4":
            case "sandwich":
            case "sandwiches":
                return SANDWICH;
            case "5":
            case "hot dog":
            case "hotdog":
            case "hot dogs":
            case "hotdogs":
                return HOT_DOG;
            default:
                return null;
        }
    }

    /**
     * @return a single line listing every food with its menu number, used by
     *         the prompts in {@code InputHelper}
     */
    public static String menuList() {
        StringBuilder sb = new StringBuilder();
        FoodType[] all = values();
        for (int i = 0; i < all.length; i++) {
            if (i > 0) {
                sb.append("  ");
            }
            sb.append(i + 1).append(". ").append(all[i].displayName);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
