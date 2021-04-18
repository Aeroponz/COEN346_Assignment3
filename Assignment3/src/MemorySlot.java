public class MemorySlot {
    // ---- CONSTRUCTOR ----
    public MemorySlot(Page initialPage, int clockTime){
        page = initialPage;
        lastAccessTime = clockTime;
    }

    public MemorySlot(){
        page = new Page();
        lastAccessTime = -1;
    }

    // ---- ACCESSORS ----
    public String getId() {
        return page.getId();
    }
    public long read(long clockTime) {
        lastAccessTime = clockTime;
        return page.getValue();
    }
    public long lastAccessTime() {
        return lastAccessTime;
    }
    public boolean pageLockState() {
        return page.getLock();
    }
    public Page getPageForSwap(){
        return page;
    }

    // ---- SETTERS ----
    public void write(Page newPage, long clockTime) {
        lastAccessTime = clockTime;
        page = newPage;
    }

    public void lock(){
        page.setLock(false);
    }

    public void unlock(){
        page.setLock(false);
    }

    
    // ---- ATTRIBUTES ----
    private Page page;
    private long lastAccessTime;
}
