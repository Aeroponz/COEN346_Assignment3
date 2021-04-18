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

    public void write(Page page) {
        appendToFile(page);
    }

    public void delete(String id){
        // Move deletes the variable on the drive after passing it to the virtual memory manager
        // if we don't return it, it will be deleted
        move(id);
    }

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
