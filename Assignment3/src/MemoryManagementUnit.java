import java.util.ArrayList;
import java.util.TimerTask;

class MemoryManagementUnit extends TimerTask {

    public void run() {
       mmu_tick();
    }

    /**
     * Called on each memory management unit tick
     */
    public static void mmu_tick() {
        if(!App.vmmCommandWaitingList.isEmpty()){
            var command = App.vmmCommandWaitingList.remove();
            //Before executing the command, make sure that process hasn't been terminated between the API call and its execution.
            switch (command.type) {
                case Store:
                    App.vmm.lock.writeLock().lock();
                    try {
                        App.vmm.Store(String.valueOf(command.variableId), command.variableValue, Clock_Tick.getClockTime());
                    } finally {
                        App.vmm.lock.writeLock().unlock();
                    }
                    break;
                
                case Release:
                    App.vmm.lock.writeLock().lock();
                    try {
                        App.vmm.Release(String.valueOf(command.variableId));
                    } finally {
                        App.vmm.lock.writeLock().unlock();
                    }
                    break;
    
                case Lookup:
                    App.vmm.lock.readLock().lock();
                    try {
                        var value = App.vmm.Lookup(String.valueOf(command.variableId), Clock_Tick.getClockTime());
                        command.variableValue = value;
                    } finally {
                        App.vmm.lock.readLock().unlock();
                    }
                    break;
            
                default:
                    throw new Error("Unkown Commands");
            }
            App.printToConsoleAndLog(command.print());
        }
    }
}
