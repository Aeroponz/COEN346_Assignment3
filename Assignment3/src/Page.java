public class Page {
    // ---- CONSTRUCTOR ----
    public Page(String Id, long Value){
        id = Id;
        value = Value; 
    }

    public Page(){
        id = "Not Found";
        value = -1;
    }

    // ---- ACCESSORS ----
    public String getId() {
        return id;
    }
    public long getValue() {
        return value;
    }
    public boolean getLock() {
        return locked;
    }

    // ---- SETTERS ----
    public void setId(String newId) {
        id = newId;
    }
    public void setValue(long newValue) {
        value = newValue;
    }
    public void setLock(boolean lockState) {
        locked = lockState;
    }

    
    // ---- ATTRIBUTES ----
    private String id;
    private long value;
    private boolean locked = false;
}
