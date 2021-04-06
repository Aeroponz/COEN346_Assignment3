import java.util.*; 
import java.nio.charset.StandardCharsets; 
import java.nio.file.*; 
import java.io.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.IntStream;

public class App {

    /* ---- GLOBAL VARS ---- */
    /* -- Input Files -- */
    private static final String commandsFileName = "commands.txt";
    private static final String memConfigFileName = "memconfig.txt";
    private static final String processesFileName = "processes.txt";

    /* -- Clock -- */
    private static int elapsedTime = 0;

    /* -- Virtual Memory Manager -- */
    private static VirtualMemoryManager vmm;

    /* -- Processes -- */
    private static ArrayList<Process> processes = new ArrayList<Process>();

    /* -- Commands -- */
    private static ArrayList<Command> commands = new ArrayList<Command>();

    /* -- CPU -- */
    private static int CPU_CORES;

    private static Process[] runningJobs;

    /* -- Scheduler -- */
    private static ArrayList<Process> readyQueue = new ArrayList<Process>();


    public static void main(String[] args) throws Exception {

        // Read input file and setup virtual memory manager
        readInputFile();
        ALogger a_logger = new ALogger();

        /* 3 Tasks -> 3 Threads. */
        // Thread 1: Clock - Timer 1000ms
        Clock_Tick checkForNewProcess = new Clock_Tick();
        // Thread 2: Memory Management Unit - Timer 1000ms (Initial delay of 50ms)
        MemoryManagementUnit memoryManagementUnit = new MemoryManagementUnit(a_logger);
        // Thread 3: Scheduler - Timer 1000ms (Initial delay of 500ms, this way scheduler doesn't overlap with memory management unit)
        Scheduler scheduler = new Scheduler(a_logger);

        // Initialise Timer and start the threads
        Timer t = new Timer();
        t.schedule(checkForNewProcess, 0, 1000);
        t.schedule(memoryManagementUnit, 50, 1000);
        t.schedule(scheduler, 500, 1000);
    }

    /**
     * Read an input file.
     * @param fileName
     */
    public static void readInputFile() {
        List<String> commandLines = Collections.emptyList();
        List<String> memConfigLines = Collections.emptyList();
        List<String> processesLines = Collections.emptyList();

        // Get Working Directory. Code is one folder lower.
        String workingDirectory = Paths.get("src").toAbsolutePath().toString();

        try {
            // Read memory config
            System.out.println(Paths.get(workingDirectory, memConfigFileName));
            memConfigLines = Files.readAllLines(Paths.get(workingDirectory, memConfigFileName), StandardCharsets.UTF_8);

            // Read processes
            System.out.println(Paths.get(workingDirectory, processesFileName));
            processesLines = Files.readAllLines(Paths.get(workingDirectory, processesFileName), StandardCharsets.UTF_8);

            // Read commands
            System.out.println(Paths.get(workingDirectory, commandsFileName));
            commandLines = Files.readAllLines(Paths.get(workingDirectory, commandsFileName), StandardCharsets.UTF_8);
        }

        catch (IOException exception) {
            exception.printStackTrace();
        }

        // Get the memory configuration and initialize the Virtual Memory Manager
        int memConfig = Integer.parseInt(memConfigLines.get(0));
        vmm = new VirtualMemoryManager(memConfig);

        // Build the list of commands
        int commandListSize = commandLines.size();
        int lineCounter = 0;

        if(commandListSize > 1){
            while(lineCounter < commandListSize){
                String[] lineSplit = commandLines.get(lineCounter++).split(" ");
                String command = lineSplit[0];
                if(command.equals("Store")){
                    commands.add(new Command(Integer.parseInt(lineSplit[1]), Integer.parseInt(lineSplit[2])));
                }
                else if(command.equals("Release")){
                    commands.add(new Command(VMemCommandType.Release, Integer.parseInt(lineSplit[1])));
                }
                else if(command.equals("Lookup")){
                    commands.add(new Command(VMemCommandType.Lookup, Integer.parseInt(lineSplit[1])));
                }
                else{
                    // Do nothing
                }
            }
        }

        // Build the list of processes
        int processListSize = processesLines.size();
        lineCounter = 0;
        if(processListSize > 1){
            CPU_CORES = Integer.parseInt(processesLines.get(lineCounter++));
            int processCount = Integer.parseInt(processesLines.get(lineCounter++));
            for(int i = 0; i < processCount; i++){
                processes.add(new Process(processesLines.get(lineCounter++)));
            }
        }
    }

    public static void printToConsoleAndLog(ALogger logger, String msg) {
        String timedStampedMsg = "Clock: " + elapsedTime + ", " + msg;
        logger.info(timedStampedMsg);
    }

/**
 * Called on each clock thread tick
 */
public static void clock_tick() {
    elapsedTime++;
}

/**
 * Called on each memory management unit tick
 */
public static void mmu_tick(ALogger a_logger) {
    App.printToConsoleAndLog(a_logger, "MMU has ticked");
}

/**
 * Called on each scheduler tick
 */
public static void scheduler_tick(ALogger a_logger) {
    // Iterate through processes list to verify if a process must be added to the ready queue
    IntStream.range(0, processes.size())
        .filter(x -> processes.get(x).getReadyTime()== elapsedTime)
        .map(x -> readyQueue.add(processes.get(x)))
        .forEach(readyQueue.add(processes.get(x)));
}
}

class ALogger {
    BufferedWriter writer;
    Logger logger;
    final String outputFileName = "output.txt";

    ALogger () throws IOException {

        this.writer = new BufferedWriter(new FileWriter(outputFileName));


        this.logger = Logger.getLogger("Virtual Memory Management Log");
        FileHandler fh;

        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler(outputFileName);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        }catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void info(String s) {
        try {
            System.out.println(s);
            this.writer.write(s + "\n");
        } catch (IOException e) {
        }
    }

    public void close() {
        try {
            this.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Clock_Tick extends TimerTask {
    public Clock_Tick() {}
    public void run() {
       App.clock_tick();
    }
}

class MemoryManagementUnit extends TimerTask {
    private final ALogger a_logger;

    public MemoryManagementUnit(ALogger a_logger) {
        this.a_logger = a_logger;
    }
    public void run() {
       App.mmu_tick(a_logger);
    }
}

class Scheduler extends TimerTask {
    private final ALogger a_logger;

    public Scheduler(ALogger a_logger) {
        this.a_logger = a_logger;
    }
    public void run() {
       App.scheduler_tick(a_logger);
    }
}
