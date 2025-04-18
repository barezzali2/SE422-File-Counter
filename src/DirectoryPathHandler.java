import java.io.File;
import java.util.Scanner;

// References: https://docs.oracle.com/javase/8/docs/api/java/io/File.html

public class DirectoryPathHandler {

    public static String getValidPath() {
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

        return directoryPath;

    }
}