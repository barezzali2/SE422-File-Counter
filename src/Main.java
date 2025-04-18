// Rawezh Dana Fadhil - rd21176@auis.edu.krd
// Barez Zuber Ali - bz20458@auis.edu.krd


// References: https://www.baeldung.com/java-blocking-queue
// https://www.tpointtech.com/blockingqueue-in-java
// https://chatgpt.com/ --> How to use put and take methods in LinkedBlockingQueue (BlockingQueue) for the results queue?

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String... args) {

        BlockingQueue<String> resultsQueue = new LinkedBlockingQueue<>();
        String directoryPath = DirectoryPathHandler.getValidPath(); // Get the valid directory path


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
        });


        MultiThreadCounter multiCounter = new MultiThreadCounter();
        ThreadPoolCounter threadPoolCounter = new ThreadPoolCounter();
        


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
        
                    // Adding a small delay
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


            // Add a final "DONE" signal to stop the results thread
            resultsQueue.put("DONE");
            resultsThread.join();
        } catch (Exception ex) {
            System.err.println(ex);
        }
        
    }
}