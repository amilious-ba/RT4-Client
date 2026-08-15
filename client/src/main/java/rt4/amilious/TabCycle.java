package rt4.amilious;

import rt4.client;

public final class TabCycle {

    /** Last sidebar tab opened, or null if none yet */
    private static Tab lastSelected = null;

    private TabCycle() {
    }

    /** From AmiliousClient.onInterfaceButton */
    public static void onComponent(int componentId) {
        Tab tab = Tab.fromComponentId(componentId);
        if (tab != null) {
            lastSelected = tab;
        }
    }

    public static Tab lastSelected() {
        return lastSelected;
    }

    /** Page Down — first tab if none selected, otherwise next */
    public static void next() {
        if (client.gameState != 30) {
            return;
        }
        Tab tab = (lastSelected == null) ? Tab.ORDER[0] : lastSelected.next();
        tab.select();
    }

    /** Page Up — last tab if none selected, otherwise previous */
    public static void previous() {
        if (client.gameState != 30) {
            return;
        }
        Tab tab = (lastSelected == null)
                ? Tab.ORDER[Tab.ORDER.length - 1]
                : lastSelected.prev();
        tab.select();
    }
}