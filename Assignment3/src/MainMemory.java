public final class MainMemory {
    // ---- CONSTRUCTOR ----
    public MainMemory(int pagesCapacity){
        slots = new MemorySlot[pagesCapacity];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new MemorySlot();
        }
    }

    /**
     * Read in main memory
     * @param slotIndex Read at index
     * @param clockTime Timestamp (ms)
     * @return variable value
     */
    public long read(int slotIndex, int clockTime){
        return slots[slotIndex].read(clockTime);
    }

    /**
     * Write to main memory
     * @param page Page to write
     * @param slotIndex Write at index
     * @param clockTime Timestamp (ms)
     */
    public void write(Page page, int slotIndex, long clockTime){
        slots[slotIndex].write(page, clockTime);
    }
    
    // ---- ATTRIBUTES ----
    public MemorySlot slots[];
}
