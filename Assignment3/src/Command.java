public class Command {
    // ---- CONSTRUCTOR ----
    public Command(int varId, long varValue){
        type = VMemCommandType.Store;
        variableId = varId;
        variableValue = varValue; 
    }

    public Command(VMemCommandType cmdType, int varId){
        type = cmdType;
        variableId = varId;
        variableValue = -1; 
    }
    
    // ---- ATTRIBUTES ----
    public VMemCommandType type;
    public int variableId;
    public long variableValue;
}
