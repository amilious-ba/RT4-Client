package rt4.amilious;

import rt4.ClientProt;
import rt4.JagString;
import rt4.client;

public enum Tab {
    COMBAT(48889897),
    STATS(48889898),
    QUESTS(48889899),
    INVENTORY(48889900),
    EQUIPMENT(48889901),
    PRAYER(48889902),
    MAGIC(48889903),
    FRIENDS(48889905),
    IGNORE(48889906),
    CLAN(48889907),
    OPTIONS(48889908),
    EMOTES(48889909),
    MUSIC(48889910);

    public final int componentId;

    /** Last known selected sidebar tab */
    private static Tab current = COMBAT;

    Tab(int componentId) {
        this.componentId = componentId;
    }

    public int index() {
        return ordinal();
    }

    public static final Tab[] ORDER = values();

    public static Tab current() {
        return current;
    }

    /**
     * Called when any interface button is pressed.
     * Updates current only if the component is a sidebar tab.
     * @return true if current tab changed
     */
    public static boolean onComponent(int componentId) {
        Tab tab = fromComponentId(componentId);
        if (tab == null || tab == current) {
            return false;
        }
        current = tab;
        return true;
    }

    public static Tab fromIndex(int index) {
        return ORDER[Math.floorMod(index, ORDER.length)];
    }

    public static Tab fromComponentId(int componentId) {
        for (Tab tab : ORDER) {
            if (tab.componentId == componentId) {
                return tab;
            }
        }
        return null;
    }

    public Tab next() {
        return fromIndex(ordinal() + 1);
    }

    public Tab prev() {
        return fromIndex(ordinal() - 1);
    }

    public void select() {
        if (client.gameState != 30) {
            return;
        }
        ClientProt.method4512(JagString.EMPTY, -1, 1, this.componentId);
        // current is updated via onComponent when method4512 runs
    }
}