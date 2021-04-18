public class Command {
    /**
     * Constructor for the Store command
     * @param varId
     * @param varValue
     */
    public Command(int varId, long varValue){
        type = VMemCommandType.Store;
        variableId = varId;
        variableValue = varValue; 
    }

    /**
     * Constructor for the Release and Lookup commands
     * @param cmdType
     * @param varId
     */
    public Command(VMemCommandType cmdType, int varId){
        type = cmdType;
        variableId = varId;
        variableValue = -1; 
    }

    /**
     * Helper function to print command info
     * @return
     */
    public String print(){
        if(type != VMemCommandType.Release){
            return "Process " + caller + ", " + type + ": Variable " + variableId + ", Value: " + variableValue;
        }
        else{
            return "Process " + caller + ", " + type + ": Variable " + variableId;
        }
    }
    
    // ---- ATTRIBUTES ----
    public VMemCommandType type;
    public int variableId;
    public long variableValue;
    public String caller;
}
