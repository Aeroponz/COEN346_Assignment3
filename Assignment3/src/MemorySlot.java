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
    public long read(int clockTime) {
        lastAccessTime = clockTime;
        return page.getValue();
    }
    public int lastAccessTime() {
        return lastAccessTime;
    }
    public boolean pageLockState() {
        return page.getLock();
    }

    // ---- SETTERS ----
    public void write(Page newPage, int clockTime) {
        lastAccessTime = clockTime;
        page = newPage;
    }

    public void toggleLock(){
        page.setLock(!page.getLock());
    }

    
    // ---- ATTRIBUTES ----
    private Page page;
    private int lastAccessTime;
}
