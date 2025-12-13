package ui;

import model.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

public class MatrixGUIController {
    private final MatrixGUI view;

    public MatrixGUIController(MatrixGUI view) {
        this.view = view;

        // Connect the View's buttons to this Controller's methods
        this.view.addRunListener(this::runBenchmark);
        this.view.addClearListener(_ -> view.clearTable());

        // Show the UI only after everything is wired up
        this.view.setVisible(true);
    }

    private void runBenchmark(ActionEvent e) {
        int size;
        int threshold;

        // Get Inputs from View
        try {
            size = view.getSelectedSize();
            threshold = view.getThreshold();
            if (threshold < 1) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            view.showError("Invalid input. Threshold must be a positive integer.");
            return;
        }

        view.setControlsEnabled(false);

        // Start Background Worker (Background عشان في ال Frezz الي كان بيحصل :))
        SwingWorker<Void, Long> worker = new SwingWorker<>() {
            private int rowIndex = -1;
            private long seqTime = 0L;
            private long parTime = 0L;

            @Override
            protected Void doInBackground() throws Exception {
                Matrix A = MatrixUtils.randomMatrix(size, size);
                Matrix B = MatrixUtils.randomMatrix(size, size);

                // Sequential Run
                MatrixMultiplier seq = new SequentialMatrixMultiplier();
                seqTime = MatrixUtils.measure(() -> seq.multiply(A, B));

                // Update View: Add row immediately
                SwingUtilities.invokeAndWait(() -> {
                    rowIndex = view.addInitialRow(size + "x" + size, threshold, seqTime);
                });

                // Parallel Run
                ForkJoinMatrixMultiplier par = new ForkJoinMatrixMultiplier(threshold);
                final long start = System.nanoTime();

                IntConsumer progressCallback = completedRows -> {
                    long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                    publish(elapsedMs);
                };

                par.multiply(A, B, progressCallback);
                parTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                return null;
            }

            @Override
            protected void process(List<Long> chunks) {
                // Live update of the timer in the table
                if (rowIndex >= 0) {
                    view.updateParallelTime(rowIndex, chunks.getLast());
                }
            }

            @Override
            protected void done() {
                try {
                    get(); // Catch any exceptions that happened in background
                    if (rowIndex >= 0) {
                        String speedupStr = "N/A";
                        if (parTime > 0) {
                            double speedup = (double) seqTime / parTime;
                            speedupStr = String.format("%.2fx", speedup);
                        }
                        view.updateParallelResults(rowIndex, parTime, speedupStr);
                    }
                } catch (Exception ex) {
                    view.showError("Error: " + ex.getMessage());
                } finally {
                    view.setControlsEnabled(true);
                }
            }
        };
        worker.execute();
    }
}