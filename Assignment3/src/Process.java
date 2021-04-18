public class Process extends Thread {
    // ---- CONSTRUCTOR ----
    public Process(String inputLine, int ident){
        String[] splitInput = inputLine.split(" ");
        readyTime = Integer.parseInt(splitInput[0]);
        serviceTime = Integer.parseInt(splitInput[1]);
        remainingTime = serviceTime; 
        identifier = String.valueOf(ident);   
    }

    public Process(){
        identifier = "";
    }

    public Thread thread;

    public void startThread() {
        thread = new Thread(new ParallelTask(identifier, remainingTime));
        thread.setName("Thread " + identifier);
        thread.start();
        status = ProcessStatus.Running;
    }

    public void pauseThread() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread = new Thread(new ParallelTask(identifier, remainingTime));
        thread.setName("Thread " + identifier);
        status = ProcessStatus.Ready;
    }

    public void stopThread() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread = new Thread(new ParallelTask(identifier, remainingTime));
        thread.setName("Thread " + identifier);
        status = ProcessStatus.Finished;
    }

    // ---- ACCESSORS ----
    public String getIdentifier() {
        return identifier;
    }
    public int getReadyTime() {
        return readyTime;
    }
    public int getServiceTime() {
        return serviceTime;
    }
    public int getRemainingTime() {
        return remainingTime;
    }
    public ProcessStatus getStatus() {
        return status;
    }

    // ---- SETTERS ----
    public void setIdentifier(String newIdentifier) {
        identifier = newIdentifier;
    }
    public void setReadyTime(int newReadyTime) {
        readyTime = newReadyTime;
    }
    public void setServiceTime(int newServiceTime) {
        serviceTime = newServiceTime;
    }
    public void setRemainingTime(int newRemainingTime) {
        remainingTime = newRemainingTime;
    }
    public void setStatus(ProcessStatus newStatus) {
        status = newStatus;
    }

    
    // ---- ATTRIBUTES ----
    private String identifier = "Unknown";
    private int readyTime = -1;
    private int serviceTime = -1;
    private int remainingTime = -1;
    private ProcessStatus status = ProcessStatus.Unknown;
}

class ParallelTask implements Runnable{

    String processName;
    int remainingTime;

    ParallelTask(String name, int remaining){
        processName = name;
        remainingTime = remaining * 1000;
    }

    @Override
    public void run() {
        executeACommand();
        long min = 900L;
        long max = 1000L;
        long random = min + (long) (Math.random() * (max - min));
        while(true){
            //App.printToConsoleAndLog("RANDOM: " + random + " REMAINING: " + remainingTime);
            if(random >= remainingTime){
                //App.printToConsoleAndLog("Break out!");
                break;
            }
            else
            {
                try {
                    //App.printToConsoleAndLog("Zzzzzzzzzzzz for " + random);
                    remainingTime -= random;
                    Thread.sleep(random);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                executeACommand();
                random = min + (long) (Math.random() * (max - min));
            }
        }
    }

    private void executeACommand(){
        var command = App.nextCommand();
        command.caller=processName;
        App.vmmCommandWaitingList.add(command);
    }
 
}
