import java.util.*;
import java.util.stream.*;

class Scheduler extends TimerTask {
    private final CustomLogger logger;

    public Scheduler(CustomLogger logger) {
        this.logger = logger;
    }
    public void run() {
       scheduler_tick(logger);
    }

    /**
     * Called on each scheduler tick
     */
    public static void scheduler_tick(CustomLogger logger) {
        updateReadyQueue(logger);
        completePreviousTick(logger);
        scheduleNextTick(logger);
    }
    /**
     * Iterate through processes list to verify if a process must be added to the ready queue
     * @param logger
     */
    private static void updateReadyQueue(CustomLogger logger){
        List<Process> processesEntering = IntStream.range(0, App.processes.size())
        .filter(x -> App.processes.get(x).getReadyTime() == Clock_Tick.getClockTicks() / 1000)
        .mapToObj(x -> App.processes.get(x))
        .collect(Collectors.toList());
        if (processesEntering.size() > 0)
        {
            App.readyQueueLock.writeLock().lock();
            try {
                for (Process process : processesEntering) {
                    App.printToConsoleAndLog("Process "+ process.getIdentifier()+" in ready queue. Service time: " + process.getServiceTime());
                    App.readyQueue.add(process);
                }
            } finally {
                App.readyQueueLock.writeLock().unlock();
            }
        }
    }
    /**
     * Update remaining time and cleanup processes that are finished
     * @param logger
     */
    private static void completePreviousTick(CustomLogger logger){
        var sizeTemp = App.runningJobs.size();
        for (int i = 0; i < sizeTemp; i++) {
            //App.printToConsoleAndLog("Process " + App.runningJobs.get(i).getIdentifier() + ": Remaining time: " + App.runningJobs.get(i).getRemainingTime());
            if(App.runningJobs.get(i).getRemainingTime() == 0){
                App.runningJobs.get(i).stopThread();
                App.printToConsoleAndLog("Process " + App.runningJobs.get(i).getIdentifier() + ": Finished.");
                App.runningJobs.remove(i);
                i--;
                sizeTemp--;
            }
        }
    }

    /**
     * Prepare jobs for next tick
     * @param logger
     */
    private static void scheduleNextTick(CustomLogger logger){
        // Schedule a process from the ready queue for each free CPU core
        for (int i = 0; i < App.CPU_CORES - App.runningJobs.size(); i++) {
            // A process is waiting to be executed
            if(App.readyQueue.size() > 0){
                App.readyQueueLock.writeLock().lock();
                try {                    
                    App.runningJobs.add(App.readyQueue.remove(0));
                    App.runningJobs.get(App.runningJobs.size()-1).startThread();
                    App.printToConsoleAndLog("Process " + App.runningJobs.get(App.runningJobs.size()-1).getIdentifier() + ": Started.");
                } finally {
                    App.readyQueueLock.writeLock().unlock();
                }
            }                
        }
        for (var process : App.runningJobs) {
            process.setRemainingTime(process.getRemainingTime() - 1);
        }
    }
}