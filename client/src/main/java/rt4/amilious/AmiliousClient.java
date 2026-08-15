package rt4.amilious;

import rt4.JagString;

/**
 * All AmiliousScape client customizations.
 * Upstream hooks should only call AmiliousClient.init() / onCanvas() / onInterfaceButton().
 */
public final class AmiliousClient {

    private static boolean initialized = false;

    /** Call once after main client init (after PluginRepository.Init()). */
    public static void init() {
        if (initialized) return;
        initialized = true;
        InputController.register(); // canvas may already exist
    }

    /** Call at end of GameShell.addCanvas() — survives canvas replace. */
    public static void onCanvas() {
        InputController.register();
    }

    /** Call at start of ClientProt.method4512. */
    public static void onInterfaceButton(int componentId) {
        TabCycle.onComponent(componentId);
        MapController.onComponent(componentId);
    }

    /** call after PluginRepository.Update(); in client.mainLoop */
    public static void update() {
        MapController.tickInput();
    }

    /**
     * @return true if the command was handled and Cheat should stop processing it
     */
    public static boolean handleCheat(JagString command) {
        // parse, handle ::borderless, etc.
        // return true when consumed
        // return false to let normal Cheat logic run
        return false;
    }


    public static void toggleTouchKeyboard() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (!os.contains("win")) {
            return; // Ally only
        }
        try {
            // Touch keyboard
            String tabTip = System.getenv("ProgramFiles")
                    + "\\Common Files\\Microsoft Shared\\ink\\TabTip.exe";
            new ProcessBuilder(tabTip).start();
        } catch (Exception e) {
            try {
                // Fallback
                new ProcessBuilder("osk.exe").start();
            } catch (Exception e2) {
                System.err.println("Could not open on-screen keyboard: " + e2);
            }
        }
    }

}