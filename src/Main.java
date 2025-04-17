import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String... args) {

        BlockingQueue<String> resultsQueue = new LinkedBlockingQueue<>();
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
            int count = counter.countPDFs(inputPath, resultsQueue, startTime);
            long duration = System.currentTimeMillis() - startTime;
            try {
                resultsQueue.put("Final: [Single Thread] Found " + count + " PDFs in " + duration + " ms");
            } catch (Exception e) {
                System.err.println("Counting was interrupted: " + e.getMessage());
            }
            // System.out.println("[Single Thread] found " + count + " in " + duration + " ms");
        });


        MultiThreadCounter multiCounter = new MultiThreadCounter();

        ThreadPoolCounter threadPoolCounter = new ThreadPoolCounter();
        
        
        // Thread multiCounterThread = new Thread(() -> {
        //     long startTime = System.currentTimeMillis();
        //     MultiThreadCounter multiCounter = new MultiThreadCounter();
        //     int count = multiCounter.countWith4Threads(inputPath, resultsQueue, startTime); // Pass resultsQueue
        //     long duration = System.currentTimeMillis() - startTime;
        //     try {
        //         resultsQueue.put("Final: [4 Threads] Found " + count + " PDFs in " + duration + " ms");
        //     } catch (Exception e) {
        //         System.err.println("Counting was interrupted: " + e.getMessage());
        //     }
        // });



        // Thread pool counting
        // Thread threadPoolCounterThread = new Thread(() -> {
        //     long startTime = System.currentTimeMillis();
        //     ThreadPoolCounter threadPoolCounter = new ThreadPoolCounter();
        //     int count = threadPoolCounter.countWithThreadPool(inputPath, resultsQueue, startTime);
        //     long duration = System.currentTimeMillis() - startTime;
        //     try {
        //         resultsQueue.put("Final: [Thread Pool] Found " + count + " PDFs in " + duration + " ms");
        //         // System.out.printf("[Thread Pool] Found %d PDFs in %d ms%n", count, duration);
        //     } catch (Exception e) {
        //         System.err.println("Counting was interrupted: " + e.getMessage());
        //     }
        // });


        Thread resultsThread = new Thread(() -> {
            try {
                while (true) {
                    String result = resultsQueue.take(); // Blocks until a result is available
                    if ("DONE".equals(result)) {
                        break; // Exit the loop when the final "DONE" signal is encountered
                    } else if ("DONE_SINGLE".equals(result)) {
                        System.out.println("[Single Thread] Counting completed.");
                        System.out.println();
                        System.out.println("----------------------");
                        System.out.println();
                    } else if ("DONE_MULTI".equals(result)) {
                        System.out.println("[4 Threads] Counting completed.");
                        System.out.println();
                        System.out.println("----------------------");
                        System.out.println();
                    } else if ("DONE_POOL".equals(result)) {
                        System.out.println("[Thread Pool] Counting completed.");
                        System.out.println();
                        System.out.println("----------------------");
                        System.out.println();
                    } else {
                        System.out.println(result); // Print progress updates
                    }
        
                    // Add a small delay for better readability
                    Thread.sleep(180);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
            }
        });
        
        
        
        try {
            // Start the results thread first
            resultsThread.start();
        
            // Start and join single-threaded counting
            singleCounterThread.start();
            singleCounterThread.join();
            resultsQueue.put("DONE_SINGLE"); 
        
            
            
            long multiStartTime = System.currentTimeMillis();
            int multiCount = multiCounter.countWith4Threads(inputPath, resultsQueue, multiStartTime);
            long multiDuration = System.currentTimeMillis() - multiStartTime;
            resultsQueue.put("Final: [4 Threads] Found " + multiCount + " PDFs in " + multiDuration + " ms");
            resultsQueue.put("DONE_MULTI"); // Signal the completion of multi-threaded counting
            
            
            
            long poolStartTime = System.currentTimeMillis();
            int poolCount = threadPoolCounter.countWithThreadPool(inputPath, resultsQueue, poolStartTime);
            long poolDuration = System.currentTimeMillis() - poolStartTime;
            resultsQueue.put("Final: [Thread Pool] Found " + poolCount + " PDFs in " + poolDuration + " ms");
            resultsQueue.put("DONE_POOL"); // Signal the completion of thread pool counting
            

            // Start and join multi-threaded counting
            // multiCounterThread.start();
            // multiCounterThread.join();
            // resultsQueue.put("DONE_MULTI"); 


            // Start and join thread pool counting
            // threadPoolCounterThread.start();
            // threadPoolCounterThread.join();
            // resultsQueue.put("DONE_POOL"); 


            // Add a final "DONE" signal to stop the results thread
            resultsQueue.put("DONE");
            resultsThread.join();
        } catch (Exception ex) {
            System.err.println(ex);
        }

        input.close();
        
    }
}