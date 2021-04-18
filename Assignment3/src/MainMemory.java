public final class MainMemory {
    // ---- CONSTRUCTOR ----
    public MainMemory(int pagesCapacity){
        slots = new MemorySlot[pagesCapacity];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new MemorySlot();
        }
    }

    public long read(int slotIndex, int clockTime){
        return slots[slotIndex].read(clockTime);
    }

    public void write(Page page, int slotIndex, long clockTime){
        slots[slotIndex].write(page, clockTime);
    }
    
    // ---- ATTRIBUTES ----
    public MemorySlot slots[];
}
