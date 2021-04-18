import java.util.TimerTask;
import java.util.concurrent.locks.*;
import java.time.*;

class Clock_Tick extends TimerTask {

    /**
     * Constructor
     */
    public Clock_Tick() {
        start = Instant.now();
        timeLock = new ReentrantReadWriteLock();

    }

    /**
     * Method ran on the clock thread
     */
    public void run() {
       clock_tick();
    }

    /**
     * Called on each clock thread tick
     * Requires write lock to increment time
     */
    public static void clock_tick() {
        timeLock.writeLock().lock();
        try {
            clockTime++;
        } finally {
            timeLock.writeLock().unlock();
        }
    }

    /**
     * Returns the floor of the time (1000ms increments).
     * Requires read lock to read clockTime.
     * @return
     */
    public static int getClockTicks(){
        timeLock.readLock().lock();
        try {
            return clockTime * 1000;
        } finally {
            timeLock.readLock().unlock();
        }
    }

    /**
     * Returns the actual clock time
     * @return
     */
    public static long getClockTime() {
        return Duration.between(start, Instant.now()).toMillis() + 1000;
    }

    private static Instant start;
    private static int clockTime;
    private static ReadWriteLock timeLock;
}