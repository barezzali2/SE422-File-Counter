import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadCounter {
    private int totalCount = 0;
    private final ReentrantLock lock = new ReentrantLock();

    public int countWith4Threads(String rootPath) {
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
                int localCount = 0;
                for (int j = start; j < end; j++) {
                    localCount += countItems(topLevelItems[j]);
                }
                
                lock.lock();
                try {
                    totalCount += localCount;
                } finally {
                    lock.unlock();
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

        return totalCount;
    }

    private int countItems(File item) {
        if (item.isFile()) {
            return item.getName().toLowerCase().endsWith(".pdf") ? 1 : 0;
        }
        
        int count = 0;
        File[] files = item.listFiles();
        if (files == null) return 0;

        for (File file : files) {
            if (file.isDirectory()) {
                count += countItems(file);
            } else if (file.getName().toLowerCase().endsWith(".pdf")) {
                count++;
            }
        }
        return count;
    }
}