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

    /**
     * Helper method used when swapping to disk storage
     * @return
     */
    public Page getPageForSwap(){
        return page;
    }

    // ---- SETTERS ----
    /**
     * Store page in memory slot
     * @param newPage Page to be stored
     * @param clockTime Timestamp (ms)
     */
    public void write(Page newPage, long clockTime) {
        lastAccessTime = clockTime;
        page = newPage;
    }

    /**
     * Lock the page. When locked, the page can't be overwritten. If a new variable
     * needs to be added to the main memory, but no page is unlocked, a swap will occur.
     * If a page is unlocked (variable has been released), the page will be squashed.
     */
    public void lock(){
        page.setLock(false);
    }

    /**
     * Unlock the page. When unlocked, the page can be overwritten. If a new variable
     * needs to be added to the main memory, but no page is unlocked, a swap will occur.
     * If a page is unlocked (variable has been released), the page will be squashed.
     */
    public void unlock(){
        page.setLock(false);
    }

    
    // ---- ATTRIBUTES ----
    private Page page;
    private long lastAccessTime;
}
