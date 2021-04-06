import java.util.ArrayList;
import java.util.stream.IntStream;

public class DiskStorage {
    // ---- CONSTRUCTOR ----
    public DiskStorage(){
        pages = new ArrayList<Page>();
    }

    public Page move(String id) {
        int index = IntStream.range(0, pages.size())
        .filter(x -> pages.get(x).getId().equals(id))
        .findFirst()
        .orElse(-1);

        if(index == -1){
            return new Page();
        }
        else{
            return pages.remove(index);
        }
    }

    public void write(Page page) {
        pages.add(page);
    }

    public void delete(String id){
        // Move deletes the variable on the drive after passing it to the virtual memory manager
        // if we don't return it, it will be deleted
        move(id);
    }

    // ---- ATTRIBUTES ----
    ArrayList<Page> pages;
}
