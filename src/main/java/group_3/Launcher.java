package group_3;

/**
 * Launcher class for running the application from a JAR file.
 * 
 * JavaFX applications that extend Application cannot be launched directly
 * from a shaded JAR due to module system restrictions. This launcher
 * class works around that limitation by calling Main.main() indirectly.
 * 
 * @author Group 3
 */
public class Launcher {
    public static void main(String[] args) {
        Main.main(args);
    }
}
