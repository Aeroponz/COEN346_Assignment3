import java.io.*;
import java.util.logging.*;

class CustomLogger {
    BufferedWriter writer;
    Logger logger;
    final String outputFileName = "output.txt";

    CustomLogger () throws IOException {

        this.writer = new BufferedWriter(new FileWriter(outputFileName));


        this.logger = Logger.getLogger("Virtual Memory Management Log");
        FileHandler fh;

        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler(outputFileName);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        }catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void info(String s) {
        try {
            System.out.println(s);
            this.writer.write(s + "\n");
        } catch (IOException e) {
        }
    }

    public void close() {
        try {
            this.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}