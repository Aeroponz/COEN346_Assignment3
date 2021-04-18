public class Page {
    // ---- CONSTRUCTOR ----
    public Page(String Id, long Value){
        id = Id;
        value = Value;
        locked = true;
    }

    public Page(){
        id = "Not Found";
        value = -1;
        locked = false;
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
    /**
     * When unlocked, the page can be overwritten. If a new variable needs to be added
     * to the main memory, but no page is unlocked, a swap will occur. If a page is 
     * unlocked (variable has been released), the page will be squashed.
     */
    public void setLock(boolean lockState) {
        locked = lockState;
    }

    
    // ---- ATTRIBUTES ----
    private String id;
    private long value;
    private boolean locked;
}
