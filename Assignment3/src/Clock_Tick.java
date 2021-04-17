import java.util.TimerTask;
import java.util.concurrent.locks.*;
import java.time.*;

class Clock_Tick extends TimerTask {
    public Clock_Tick() {
        start = Instant.now();
        timeLock = new ReentrantReadWriteLock();

    }
    public void run() {
       clock_tick();
    }

    /**
     * Called on each clock thread tick
     */
    public static void clock_tick() {
        timeLock.writeLock().lock();
        try {
            clockTime++;
        } finally {
            timeLock.writeLock().unlock();
        }
    }

    public static int getClockTicks(){
        timeLock.readLock().lock();
        try {
            return clockTime * 1000;
        } finally {
            timeLock.readLock().unlock();
        }
    }

    public static long getClockTime() {
        return Duration.between(start, Instant.now()).toMillis() + 1000;
    }

    private static Instant start;
    private static int clockTime;
    private static ReadWriteLock timeLock;
}