import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MatrixGUI extends JFrame {

    private JComboBox<String> sizeBox;
    private JTextField thresholdField;
    private DefaultTableModel tableModel;

    public MatrixGUI() {
        setTitle("Matrix Multiplication Benchmark");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        addControls();
        addResultsTable();

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MatrixGUI::new);
    }

    /**
     * Creates the control panel with input fields and run button.
     */
    private void addControls() {
        JPanel top = new JPanel(new FlowLayout());

        sizeBox = new JComboBox<>(new String[]{"128", "256", "512", "1024"});
        thresholdField = new JTextField("64", 6);

        JButton runButton = new JButton("Run Test");
        runButton.addActionListener(this::runBenchmark);

        JButton clearButton = new JButton("Clear Results");
        clearButton.addActionListener(e -> tableModel.setRowCount(0));

        top.add(new JLabel("Matrix Size:"));
        top.add(sizeBox);

        top.add(new JLabel("Threshold:"));
        top.add(thresholdField);

        top.add(runButton);
        top.add(clearButton);

        add(top, BorderLayout.NORTH);
    }

    /**
     * Creates the results table to display benchmark data.
     */
    private void addResultsTable() {
        String[] columns = {"Matrix Size", "Threshold", "Sequential (ms)", "Parallel (ms)", "Speedup"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable resultsTable = new JTable(tableModel);

        resultsTable.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Runs the benchmark with current settings and updates the results table.
     */
    private void runBenchmark(ActionEvent e) {
        int size;
        int threshold;

        // Parse and validate input
        try {
            size = Integer.parseInt((String) sizeBox.getSelectedItem());
            threshold = Integer.parseInt(thresholdField.getText());

            if (threshold < 1) {
                throw new NumberFormatException("Threshold must be positive");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input. Threshold must be a positive integer.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Generate random matrices
            Matrix A = MatrixUtils.randomMatrix(size, size);
            Matrix B = MatrixUtils.randomMatrix(size, size);

            // Create multipliers
            MatrixMultiplier seq = new SequentialMatrixMultiplier();
            MatrixMultiplier par = new ForkJoinMatrixMultiplier(threshold);

            // Measure time
            long seqTime = MatrixUtils.measure(() -> seq.multiply(A, B));
            long parTime = MatrixUtils.measure(() -> par.multiply(A, B));

            double speedup = (double) seqTime / parTime;

            // Add result to table
            tableModel.addRow(new Object[]{
                    size + "x" + size,
                    threshold,
                    seqTime,
                    parTime,
                    String.format("%.2fx", speedup)
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error during benchmark: " + ex.getMessage(),
                    "Benchmark Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}