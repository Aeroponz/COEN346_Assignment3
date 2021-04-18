import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.IntStream;

public final class DiskStorage {
    // ---- CONSTRUCTOR ----
    public DiskStorage(){
        vm = new File("vm.txt");
        createVmFile();
    }

    /**
     * Moves a page from disk to main memory
     * @param id Page Id to be returned
     * @return Page stored in disk storage
     */
    public Page move(String id) {
        var pages = readFile();
        int index = IntStream.range(0, pages.size())
        .filter(x -> pages.get(x).getId().equals(id))
        .findFirst()
        .orElse(-1);

        if(index == -1){
            return new Page();
        }
        else{
            var output = pages.remove(index);
            writeToFile(pages);
            return output;
        }
    }

    /**
     * Add a page to the disk storage
     * @param page
     */
    public void write(Page page) {
        appendToFile(page);
    }

    /**
     * Delete a page from the disk storage
     * @param id Page to be deleted
     */
    public void delete(String id){
        // Move deletes the variable on the drive after passing it to the virtual memory manager
        // if we don't return it, it will be deleted
        move(id);
    }

    /**
     * Helper function used at instantiation to delete (if possible) the old vm.txt file
     * and create the new one.
     */
    private void createVmFile(){
        try {
            if (vm.delete()) { 
                System.out.println("--- Deleted the existing VM file ---");
            }
            if (vm.createNewFile()) {
              System.out.println("--- New VM file created ---");
            }
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }

    /**
     * Helper function to append a page to the disk storage
     * @param page Page to be added
     */
    private void appendToFile(Page page){
        try {
            FileWriter vm = new FileWriter("vm.txt");
            vm.append(page.getId() + " " + page.getValue() + "\n");
            vm.close();
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }

    /**
     * Overrides all the vm.txt file with the modified pages data
     * @param pages Parsed and (possibly modified) pages data
     */
    private void writeToFile(ArrayList<Page> pages) {
        try {
            FileWriter vm = new FileWriter("vm.txt");
            for (Page page : pages) {
                vm.write(page.getId() + " " + page.getValue() + "\n");
            }
            vm.close();
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }

    /**
     * Returns all pages found in the vm.txt file
     * @return
     */
    private ArrayList<Page> readFile(){
        var output = new ArrayList<Page>();
        try {
            Scanner vmReader = new Scanner(vm);
            while (vmReader.hasNextLine()) {
              String data = vmReader.nextLine();
              var splitData = data.split(" ");
              output.add(new Page(splitData[0], Long.parseLong(splitData[1])));
            }
            vmReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return output;
    }

    // ---- ATTRIBUTES ----
    ArrayList<Page> pages;
    File vm;
}
