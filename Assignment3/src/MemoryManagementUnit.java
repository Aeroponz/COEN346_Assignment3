import java.util.TimerTask;

class MemoryManagementUnit extends TimerTask {
    private final CustomLogger logger;

    public MemoryManagementUnit(CustomLogger logger) {
        this.logger = logger;
    }
    public void run() {
       mmu_tick(logger);
    }

    /**
     * Called on each memory management unit tick
     */
    public static void mmu_tick(CustomLogger logger) {
        App.printToConsoleAndLog("MMU has ticked");
    }
}
