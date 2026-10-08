package foodstorage.datastructure;

import foodstorage.model.FoodItem;

/**
 * One match returned by a search: the tray that matched and where it sits.
 *
 * <p>A record is used because this is pure data with no behaviour - Java
 * generates the constructor, the getters, equals, hashCode and toString.</p>
 *
 * <p>{@code position} is 1-based and counted from the front, so position 1 is
 * the tray at the top of the stack / the front of the queue. The user sees a
 * position they can relate to the physical unit, not an array index.</p>
 *
 * @param item     the tray that matched the search
 * @param position 1-based position from the front, 1 to 8
 * @author Member 1
 */
public record SearchResult(FoodItem item, int position) {

    /**
     * @return the match formatted for the console, e.g.
     *         "Position 2 (from front): Pizza | 350.0 g | ..."
     */
    public String toDisplayString() {
        return String.format("Position %d (from front): %s", position, item.toDisplayString());
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}
