package ui;//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.function.IntConsumer;
//
//public class ui.MatrixGUI extends JFrame {
//
//    private JComboBox<String> sizeBox;
//    private JTextField thresholdField;
//    private DefaultTableModel tableModel;
//
//    public ui.MatrixGUI() {
//        setTitle("model.Matrix Multiplication Benchmark");
//        setSize(750, 450);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLayout(new BorderLayout(10, 10));
//        setLocationRelativeTo(null);
//
//        addControls();
//        addResultsTable();
//
//        setVisible(true);
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(ui.MatrixGUI::new);
//    }
//
//
//    private void addControls() {
//        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
//
//        sizeBox = new JComboBox<>(new String[]{"128", "256", "512", "1024"});
//        thresholdField = new JTextField("64", 6);
//
//        JButton runButton = new JButton("Run Test");
//        runButton.addActionListener(this::runBenchmark);
//
//        JButton clearButton = new JButton("Clear Results");
//        clearButton.addActionListener(e -> tableModel.setRowCount(0));
//
//        top.add(new JLabel("model.Matrix Size:"));
//        top.add(sizeBox);
//
//        top.add(new JLabel("Threshold:"));
//        top.add(thresholdField);
//
//        top.add(runButton);
//        top.add(clearButton);
//
//        add(top, BorderLayout.NORTH);
//    }
//
//    private void addResultsTable() {
//        String[] columns = {"model.Matrix Size", "Threshold", "Sequential (ms)", "Parallel (ms)", "Speedup"};
//        tableModel = new DefaultTableModel(columns, 0);
//        JTable resultsTable = new JTable(tableModel);
//
//        resultsTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
//        resultsTable.setRowHeight(25);
//
//        JScrollPane scrollPane = new JScrollPane(resultsTable);
//        add(scrollPane, BorderLayout.CENTER);
//    }
//
//    private void runBenchmark(ActionEvent e) {
//        int size;
//        int threshold;
//
//        try {
//            String selectedSize = (String) sizeBox.getSelectedItem();
//            if (selectedSize == null) throw new NumberFormatException("No matrix size selected");
//            size = Integer.parseInt(selectedSize);
//            threshold = Integer.parseInt(thresholdField.getText());
//
//            if (threshold < 1) {
//                throw new NumberFormatException("Threshold must be positive");
//            }
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this,
//                    "Invalid input. Threshold must be a positive integer.",
//                    "Input Error",
//                    JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        setControlsEnabled(false);
//
//        SwingWorker<Void, Long> worker = new SwingWorker<>() {
//            private int rowIndex = -1;
//            private long seqTime = 0L;
//            private long parTime = 0L;
//
//            @Override
//            protected Void doInBackground() throws Exception {
//                model.Matrix A = model.MatrixUtils.randomMatrix(size, size);
//                model.Matrix B = model.MatrixUtils.randomMatrix(size, size);
//
//                model.MatrixMultiplier seq = new model.SequentialMatrixMultiplier();
//                seqTime = model.MatrixUtils.measure(() -> seq.multiply(A, B));
//
//                SwingUtilities.invokeAndWait(() -> {
//                    rowIndex = tableModel.getRowCount();
//                    tableModel.addRow(new Object[]{
//                            size + "x" + size,
//                            threshold,
//                            seqTime,
//                            "-",
//                            "-"
//                    });
//                });
//
//                model.ForkJoinMatrixMultiplier par = new model.ForkJoinMatrixMultiplier(threshold);
//
//                final long start = System.nanoTime();
//
//                IntConsumer progressCallback = new IntConsumer() {
//                    @Override
//                    public void accept(@SuppressWarnings("unused") int completedRows) {
//                        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
//                        publish(elapsedMs);
//                    }
//                };
//
//                par.multiply(A, B, progressCallback);
//
//                long end = System.nanoTime();
//                parTime = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(end - start);
//
//                return null;
//            }
//
//            @Override
//            protected void process(List<Long> chunks) {
//                if (rowIndex < 0) return;
//
//                long elapsed = chunks.get(chunks.size() - 1);
//                tableModel.setValueAt(elapsed, rowIndex, 3);
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    get();
//
//                    if (rowIndex >= 0) {
//                        tableModel.setValueAt(parTime, rowIndex, 3);
//                        if (parTime > 0) {
//                            double speedup = (double) seqTime / parTime;
//                            tableModel.setValueAt(String.format("%.2fx", speedup), rowIndex, 4);
//                        } else {
//                            tableModel.setValueAt("N/A", rowIndex, 4);
//                        }
//                    }
//                } catch (Exception ex) {
//                    JOptionPane.showMessageDialog(ui.MatrixGUI.this,
//                            "Error during benchmark: " + ex.getMessage(),
//                            "Benchmark Error",
//                            JOptionPane.ERROR_MESSAGE);
//                } finally {
//                    setControlsEnabled(true);
//                }
//            }
//        };
//
//
//        worker.execute();
//    }
//
//    private void setControlsEnabled(boolean enabled) {
//        sizeBox.setEnabled(enabled);
//        thresholdField.setEnabled(enabled);
//    }
//}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class MatrixGUI extends JFrame {
    private JComboBox<String> sizeBox;
    private JTextField thresholdField;
    private DefaultTableModel tableModel;
    private JButton runButton;
    private JButton clearButton;

    public MatrixGUI() {
        setTitle("model.Matrix Multiplication Benchmark");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        addControls();
        addResultsTable();
    }

    private void addControls() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        sizeBox = new JComboBox<>(new String[]{"128", "256", "512", "1024"});
        thresholdField = new JTextField("64", 6);
        runButton = new JButton("Run Test");
        clearButton = new JButton("Clear Results");

        top.add(new JLabel("model.Matrix Size:"));
        top.add(sizeBox);
        top.add(new JLabel("Threshold:"));
        top.add(thresholdField);
        top.add(runButton);
        top.add(clearButton);

        add(top, BorderLayout.NORTH);
    }

    private void addResultsTable() {
        String[] columns = {"model.Matrix Size", "Threshold", "Sequential (ms)", "Parallel (ms)", "Speedup"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable resultsTable = new JTable(tableModel);
        resultsTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultsTable.setRowHeight(25);
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);
    }

    public void addRunListener(ActionListener listener) {
        runButton.addActionListener(listener);
    }

    public void addClearListener(ActionListener listener) {
        clearButton.addActionListener(listener);
    }

    public int getSelectedSize() {
        return Integer.parseInt((String) sizeBox.getSelectedItem());
    }

    public int getThreshold() throws NumberFormatException {
        return Integer.parseInt(thresholdField.getText());
    }

    public void setControlsEnabled(boolean enabled) {
        sizeBox.setEnabled(enabled);
        thresholdField.setEnabled(enabled);
        runButton.setEnabled(enabled);
    }

    public void clearTable() {
        tableModel.setRowCount(0);
    }

    public int addInitialRow(String size, int threshold, long seqTime) {
        tableModel.addRow(new Object[]{size, threshold, seqTime, "-", "-"});
        return tableModel.getRowCount() - 1;
    }

    public void updateParallelResults(int rowIndex, long parTime, String speedup) {
        tableModel.setValueAt(parTime, rowIndex, 3);
        tableModel.setValueAt(speedup, rowIndex, 4);
    }

    // Updates just the time (useful for live updates if needed)
    public void updateParallelTime(int rowIndex, long time) {
        tableModel.setValueAt(time, rowIndex, 3);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}