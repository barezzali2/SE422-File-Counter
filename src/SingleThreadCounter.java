import java.io.File;


// Option 1
public class SingleThreadCounter{
    private int pdfCount = 0;

    // returning the files numbers
    public int countPDFs(String directoryPath) {
        File directory = new File(directoryPath);
        if(!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        countPDFsRecursively(directory);
        return pdfCount;
    }


    // Going through them and incrementing
    public void countPDFsRecursively(File directory) {
        File[] files = directory.listFiles();
        if(files == null) return;

        for(File file : files) {
            if(file.isDirectory()) {
                countPDFsRecursively(file);
            } else if(file.getName().toLowerCase().endsWith(".pdf")) {
                pdfCount++;
            }
        }
    }
}




// Option 2
// public class SingleThreadCounter implements Runnable{
//     private int pdfCount = 0;
//     private final String directoryPath;

//     public SingleThreadCounter(String directoryPath) {
//         this.directoryPath = directoryPath;
//     }

//     @Override
//     public void run() {
//         File directory = new File(directoryPath);
//         if(!directory.exists() || !directory.isDirectory()) {
//             throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
//         }

//         countPDFsRecursively(directory);
//         System.out.println("The number of pdf files in this directory is " + pdfCount);

//     }


//     public void countPDFsRecursively(File directory) {
//         File[] files = directory.listFiles();
//         if(files == null) return;

//         for(File file : files) {
//             if(file.isDirectory()) {
//                 countPDFsRecursively(file);
//             } else if(file.getName().toLowerCase().endsWith(".pdf")) {
//                 pdfCount++;
//             }
//         }
//     }
 
// }
