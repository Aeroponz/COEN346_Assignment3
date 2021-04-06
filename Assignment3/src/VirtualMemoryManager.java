import java.util.stream.IntStream;

public class VirtualMemoryManager {
    // ---- CONSTRUCTOR ----
    public VirtualMemoryManager(int pagesCapacity){
        mainmemory = new MainMemory(pagesCapacity);
        disk = new DiskStorage();
    }

    public void Store(String variableId, long variableValue, int clockTime){
        Page pageToStore = new Page(variableId, variableValue);

        int firstFreeIndexInMainMemory = getFirstFreeMainMemorySlotIndex();
        // There is a free slot in main memory, store it there
        if(firstFreeIndexInMainMemory != -1){
            // Check that lock is still opened
            if(mainmemory.slots[firstFreeIndexInMainMemory].pageLockState()){
                mainmemory.slots[firstFreeIndexInMainMemory].toggleLock();
                mainmemory.slots[firstFreeIndexInMainMemory].write(pageToStore, clockTime);
            }
            // The slot has been stolen by another process, try again
            else{
                Store(variableId, variableValue, clockTime);
            }
        }
        else{
            disk.write(pageToStore);
        }
    }

    public void Release(String variableId){
        int indexInMainMemory = getIndexOfPageById_MainMemory(variableId);
        // Variable to be released is in main memory
        if(indexInMainMemory != 1){
            // Toggle the lock, this will allow it to be overriden by the next page that needs to be loaded in the main memory
            mainmemory.slots[indexInMainMemory].toggleLock();
        }
        // Variable is in the disk. Delete it
        else{
            disk.delete(variableId);
        }
    }

    public long Lookup(String variableId, int clockTime){
        int index = getIndexOfPageById_MainMemory(variableId);

        // Variable is already in main memory, read it
        if(index != -1){
            return mainmemory.slots[index].read(clockTime);
        }
        // Page fault! Move from disk
        else{
            Page temp = disk.move(variableId);
            // Variable does not exist
            if(temp.getId() == "Not Found"){
                return -1;
            }
            // Variable exists, move it and read
            else{
                loadIntoMainMemory(temp, clockTime);
                return Lookup(variableId, clockTime);
            }
        }
    }

    private int getFirstFreeMainMemorySlotIndex(){
        return IntStream.range(0, mainmemory.slots.length)
        .filter(x -> !mainmemory.slots[x].pageLockState())
        .findFirst()
        .orElse(-1);
    }

    private int getIndexOfPageById_MainMemory(String id){
        return IntStream.range(0, mainmemory.slots.length)
        .filter(x -> mainmemory.slots[x].getId().equals(id))
        .findFirst()
        .orElse(-1);
    }

    private void loadIntoMainMemory(Page pageToLoad, int clockTime){
        int firstFreeIndexInMainMemory = getFirstFreeMainMemorySlotIndex();
        // There is a free slot in main memory, store it there
        if(firstFreeIndexInMainMemory != -1){
            // Check that lock is still opened
            if(mainmemory.slots[firstFreeIndexInMainMemory].pageLockState()){
                mainmemory.slots[firstFreeIndexInMainMemory].toggleLock();
                mainmemory.slots[firstFreeIndexInMainMemory].write(pageToLoad, clockTime);
            }
            // The slot has been stolen by another process, try again
            else{
                loadIntoMainMemory(pageToLoad, clockTime);
            }
        }
        //There is no free page in main memory. Swap with smallest last accessed time
        else{
            int indexOfSmallestLastAccessTime = 0;
            int smallestLastAccessTime = mainmemory.slots[0].lastAccessTime();
            for (int i = 1; i < mainmemory.slots.length; i++) {
                if(mainmemory.slots[i].lastAccessTime() < smallestLastAccessTime){
                    indexOfSmallestLastAccessTime = i;
                    smallestLastAccessTime = mainmemory.slots[i].lastAccessTime();
                }
            }
            mainmemory.write(pageToLoad, indexOfSmallestLastAccessTime, clockTime);
        }
    }
    
    // ---- ATTRIBUTES ----
    private MainMemory mainmemory;
    private DiskStorage disk;
}
