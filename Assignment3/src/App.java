import java.util.*;
import java.util.concurrent.locks.*;
import java.nio.charset.StandardCharsets; 
import java.nio.file.*; 
import java.io.*;

public class App {

    /* ---- GLOBAL VARS ---- */
    /* -- Input Files -- */
    private static final String commandsFileName = "commands.txt";
    private static final String memConfigFileName = "memconfig.txt";
    private static final String processesFileName = "processes.txt";

    /* -- Output File -- */
    public static CustomLogger logger;

    /* -- Virtual Memory Manager -- */
    public static VirtualMemoryManager vmm;

    /* -- Processes -- */
    public static ArrayList<Process> processes = new ArrayList<Process>();

    /* -- Commands -- */
    public static Queue<Command> commands = new LinkedList<Command>();
    public static Queue<Command> vmmCommandWaitingList = new LinkedList<Command>();

    /* -- CPU -- */
    public static int CPU_CORES;
    public static ArrayList<Process> runningJobs = new ArrayList<Process>();

    /* -- Scheduler -- */
    public static ArrayList<Process> readyQueue = new ArrayList<Process>();
    public static ReadWriteLock readyQueueLock = new ReentrantReadWriteLock();


    public static void main(String[] args) throws Exception {

        // Read input file and setup virtual memory manager
        readInputFile();
        logger = new CustomLogger();

        /* 3 Tasks -> 3 Threads. */
        // Thread 1: Clock - Timer 1000ms
        Clock_Tick clock = new Clock_Tick();
        // Thread 2: Memory Management Unit - Timer 50ms (Initial delay of 20ms)
        MemoryManagementUnit memoryManagementUnit = new MemoryManagementUnit();
        // Thread 3: Scheduler - Timer 1000ms (Initial delay of 10ms, this way scheduler doesn't overlap with clock)
        Scheduler scheduler = new Scheduler();

        // Initialise Timer and start the threads
        Timer t = new Timer();
        t.schedule(clock, 0, 1000);
        t.schedule(scheduler, 10, 1000);
        t.schedule(memoryManagementUnit, 20, 50);
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
                processes.add(new Process(processesLines.get(lineCounter++), i+1));
            }
        }
    }

    /**
     * Helper function to print to log with clock time appended
     * @param msg Message to print to log
     */
    public static void printToConsoleAndLog(String msg) {
        String timedStampedMsg = "Clock: " + Clock_Tick.getClockTime() + ", " + msg;
        logger.info(timedStampedMsg);
    }

    /**
     * Returns the next command from the commands list for a process to execute
     * @return Command to execute
     */
    public static Command nextCommand(){
        if(!commands.isEmpty()){
            var next = commands.remove();
            commands.add(next);
            return next;
        }
        else throw new Error("No commands remaining");
    }
}
