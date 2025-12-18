package ui;

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

        top.add(new JLabel("Matrix Size:"));
        top.add(sizeBox);
        top.add(new JLabel("Threshold:"));
        top.add(thresholdField);
        top.add(runButton);
        top.add(clearButton);

        add(top, BorderLayout.NORTH);
    }

    private void addResultsTable() {
        String[] columns = {"Matrix Size", "Threshold", "Sequential (ms)", "Parallel (ms)", "Speedup"};
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