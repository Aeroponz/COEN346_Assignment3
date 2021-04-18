class ParallelTask implements Runnable{

    /**
     * Constructor
     * @param name Process name
     * @param remaining Remaining process time
     */
    ParallelTask(String name, int remaining){
        processName = name;
        remainingTime = remaining * 1000;
    }

    /**
     * Method executed by the simulated process
     */
    @Override
    public void run() {
        executeACommand();
        long min = 1L;
        long max = 1000L;
        long random = min + (long) (Math.random() * (max - min));
        while(true){
            if(random >= remainingTime){
                break;
            }
            else
            {
                try {
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

    /**
     * Gets the next command and executes it
     */
    private void executeACommand(){
        var command = App.nextCommand();
        command.caller=processName;
        App.vmmCommandWaitingList.add(command);
    }

    /**
     * Attributes
     */
    String processName;
    int remainingTime;
 
}
