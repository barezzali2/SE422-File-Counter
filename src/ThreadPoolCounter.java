import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolCounter {
    private int pdfCount = 0; // Shared cumulative count
    private final ReentrantLock lock = new ReentrantLock(); // Lock for thread-safe updates

    public int countWithThreadPool(String rootPath, BlockingQueue<String> resultsQueue, long startTime) {
        File rootDir = new File(rootPath);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory: " + rootPath);
        }

        File[] topLevelItems = rootDir.listFiles();
        if (topLevelItems == null) return 0;

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int threadPoolSize = Math.max(1, availableProcessors / 2); // Use half of the available CPUs
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        for (File item : topLevelItems) {
            executor.execute(() -> {
                countItems(item, resultsQueue, startTime); // Pass startTime
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait for all tasks to finish
        }

        return pdfCount;
    }

    private void countItems(File item, BlockingQueue<String> resultsQueue, long startTime) {
        if (item.isFile()) {
            if (item.getName().toLowerCase().endsWith(".pdf")) {
                lock.lock(); // Lock before updating the shared count
                try {
                    pdfCount++; // Increment the shared cumulative count
                    long elapsedTime = System.currentTimeMillis() - startTime; // Calculate elapsed time
                    resultsQueue.put("[Thread Pool] Found " + pdfCount + " PDF in " + elapsedTime + " ms");
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
            countItems(file, resultsQueue, startTime); // Pass startTime recursively
        }
    }
}