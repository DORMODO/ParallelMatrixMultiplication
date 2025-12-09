import ui.MatrixGUI;
import ui.MatrixGUIController;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MatrixGUI view = new MatrixGUI();
            new MatrixGUIController(view);
        });
    }
}
