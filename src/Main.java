// Rawezh Dana Fadhil - rd21176@auis.edu.krd
// Barez Zuber Ali - bz20458@auis.edu.krd

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String... args) {

        Scanner input = new Scanner(System.in);

        System.out.println("Please enter a path of an existing directory: ");
        String directoryPath = input.nextLine().trim();

        File directory = new File(directoryPath);
        while(!directory.exists() || !directory.isDirectory()) {
            System.out.println("Invalid directory. Please enter a valid directory: ");
            directoryPath = input.nextLine().trim();
            directory = new File(directoryPath);
        }
        System.out.println("Thanks! Valid directory! " + directory.getAbsolutePath());


        // Option 1 
        final String inputPath = directoryPath; //  Making the directory path final because of the lamdba expression
        Thread counterThread = new Thread(() -> {
            SingleThreadCounter counter = new SingleThreadCounter();
            int count = counter.countPDFs(inputPath);
            System.out.println("found " + count);
        });

        counterThread.start();

        try{
            counterThread.join();
        }catch(Exception ex) {
            System.err.println(ex);
        }


        // Option 2
        // SingleThreadCounter counterThread = new SingleThreadCounter(directoryPath);
        // Thread singleThread = new Thread(counterThread);
        // singleThread.start();
        // try{
        //     singleThread.join();
        // }catch(Exception ex) {
        //     System.err.println(ex);
        // }
        
    }
}
