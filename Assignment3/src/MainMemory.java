public class MainMemory {
    // ---- CONSTRUCTOR ----
    public MainMemory(int pagesCapacity){
        slots = new MemorySlot[pagesCapacity];
    }

    public long read(int slotIndex, int clockTime){
        return slots[slotIndex].read(clockTime);
    }

    public void write(Page page, int slotIndex, int clockTime){
        slots[slotIndex].write(page, clockTime);
    }
    
    // ---- ATTRIBUTES ----
    public MemorySlot slots[];
}
