import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

// References: https://www.tpointtech.com/java-arraylist

public class MultiThreadCounter {
    private int pdfCount = 0;
    private final ReentrantLock lock = new ReentrantLock();

    public int countWith4Threads(String rootPath, BlockingQueue<String> resultsQueue, long startTime) {
        File rootDir = new File(rootPath);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory: " + rootPath);
        }

        File[] topLevelItems = rootDir.listFiles();
        if (topLevelItems == null) return 0;

        List<Thread> threads = new ArrayList<>();
        int itemsPerThread = (int) Math.ceil(topLevelItems.length / 4.0);

        for (int i = 0; i < 4; i++) {
            int start = i * itemsPerThread;
            int end = Math.min(start + itemsPerThread, topLevelItems.length);
            
            Thread thread = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    countItems(topLevelItems[j], resultsQueue, startTime); // Pass resultsQueue
                }
            });


            threads.add(thread);
            thread.start();

        }
            for (Thread thread : threads) {
                try{
                    thread.join();
                }catch(Exception ex) {
                    System.err.println(ex);
                }
            }

        return pdfCount;
    }

    private void countItems(File item, BlockingQueue<String> resultsQueue, long startTime) {
        if (item.isFile()) {
            if (item.getName().toLowerCase().endsWith(".pdf")) {
                lock.lock(); // Lock before updating the shared count
                try {
                    pdfCount++; // Increment the shared cumulative count
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    resultsQueue.put("[4 Threads] Found " + pdfCount + " PDFs in " + elapsedTime + " ms");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock(); // Unlock after updating
                }
                return;
            }
            return;
        }

        File[] files = item.listFiles();
        if (files == null) return;

        for (File file : files) {
            countItems(file, resultsQueue, startTime); // Pass resultsQueue recursively
        }
    }
}