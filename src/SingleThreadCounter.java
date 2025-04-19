import java.io.File;
import java.util.concurrent.BlockingQueue;


public class SingleThreadCounter{
    private int pdfCount = 0;

    // returning the files numbers
    public int countPDFs(String directoryPath, BlockingQueue<String> resultsQueue, long startTime) {
        File directory = new File(directoryPath);
        if(!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        countPDFsRecursively(directory, resultsQueue, startTime);
        return pdfCount;
    }


    // Going through them and incrementing
    public void countPDFsRecursively(File directory, BlockingQueue<String> resultsQueue, long startTime) {
        File[] files = directory.listFiles();
        if(files == null) return;

        for(File file : files) {
            if(file.isDirectory()) {
                countPDFsRecursively(file, resultsQueue, startTime);
            } else if(file.getName().toLowerCase().endsWith(".pdf")) {
                pdfCount++;
                long elapsedTime = System.currentTimeMillis() - startTime;
                
                try {
                    resultsQueue.put("[Single Thread] found " + pdfCount + " PDFs in " + elapsedTime + " ms");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); 
                }
            }
        }
    }
}