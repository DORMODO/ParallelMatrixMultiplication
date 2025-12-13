import model.MatrixBenchmark;
import test.MatrixTest;
import ui.MatrixGUI;
import ui.MatrixGUIController;

import javax.swing.SwingUtilities;

public class RunAll {

    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("PARALLEL MATRIX MULTIPLICATION");
        System.out.println("=".repeat(70));

        // Step 1: Correctness Tests
        System.out.println("\n[STEP 1/3] Running Correctness Tests...\n");
        MatrixTest.main(args);

        // Step 2: Performance Benchmarks
        System.out.println("\n[STEP 2/3] Running Performance Benchmarks...\n");
        MatrixBenchmark.main(args);

        // Step 3: Launch GUI
        System.out.println("\n[STEP 3/3] Launching GUI Application...\n");
        SwingUtilities.invokeLater(() -> {
            MatrixGUI view = new MatrixGUI();
            new MatrixGUIController(view);
        });

        System.out.println("All tests complete. GUI is now running.");
        System.out.println("=".repeat(70) + "\n");
    }
}