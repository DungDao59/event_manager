package launcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppLauncher {
    public static void main(String[] args) {
        try {
            // Get the directory where this launcher is located
            String jarPath = AppLauncher.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath();
            File jarFile = new File(jarPath);
            String baseDir = jarFile.getParent();
            
            // Build the java command
            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("--module-path");
            command.add(baseDir + File.separator + "target" + File.separator + "lib");
            command.add("--add-modules");
            command.add("javafx.controls,javafx.fxml,javafx.graphics");
            command.add("-jar");
            command.add(baseDir + File.separator + "assignment2-1.0-SNAPSHOT.jar");
            
            // Start the process
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(baseDir));
            pb.inheritIO(); // Show console output
            Process process = pb.start();
            
            // Wait for the application to finish
            int exitCode = process.waitFor();
            System.exit(exitCode);
            
        } catch (Exception e) {
            System.err.println("Failed to launch application: " + e.getMessage());
            e.printStackTrace();
            System.err.println("\nPress Enter to exit...");
            try {
                System.in.read();
            } catch (IOException ex) {
                // Ignore
            }
            System.exit(1);
        }
    }
}
