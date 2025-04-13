

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
        Thread singleCounterThread = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            SingleThreadCounter counter = new SingleThreadCounter();
            int count = counter.countPDFs(inputPath);
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[Single Thread] found " + count + " in " + duration + " ms");
        });

        singleCounterThread.start();


        Thread multiCounterThread = new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();
                MultiThreadCounter multiCounter = new MultiThreadCounter();
                int counter = multiCounter.countWith4Threads(inputPath);
                // int count = new MultiThreadCounter().countWith4Threads(inputPath);
                long duration = System.currentTimeMillis() - startTime;
                System.out.printf("[4 Threads] Found %d PDFs in %d ms%n", counter, duration);
            } catch (InterruptedException e) {
                System.err.println("Counting was interrupted: " + e.getMessage());
            }
        });


        try{
            singleCounterThread.join();
            multiCounterThread.start();
            multiCounterThread.join();
        }catch(Exception ex) {
            System.err.println(ex);
        }


        
    }
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