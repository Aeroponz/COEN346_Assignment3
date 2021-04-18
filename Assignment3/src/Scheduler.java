import java.util.*;
import java.util.stream.*;

/**
 * This class simulates the scheduler. It extends TimerTask to facilitate the periodicity of this work.
 */
class Scheduler extends TimerTask {
    public void run() {
       scheduler_tick();
    }

    /**
     * Called on each scheduler tick
     */
    public static void scheduler_tick() {
        updateReadyQueue();
        completePreviousTick();
        scheduleNextTick();
    }
    /**
     * Iterate through processes list to verify if a process must be added to the ready queue
     */
    private static void updateReadyQueue(){
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
                for (Process process : processesEntering) {
                    App.processes.remove(process);
                }
            } finally {
                App.readyQueueLock.writeLock().unlock();
            }
        }
    }
    /**
     * Cleanup processes that are finished
     */
    private static void completePreviousTick(){
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
     */
    private static void scheduleNextTick(){
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
        // Adjust remaining time for next tick
        for (var process : App.runningJobs) {
            process.setRemainingTime(process.getRemainingTime() - 1);
        }
    }
}