// Parallel Approach (What We're Building)
// Split work across threads:
//
// Thread 1: Rows 0-255   ━━━━━━━━━━━━
// Thread 2: Rows 256-511 ━━━━━━━━━━━━  } All running
// Thread 3: Rows 512-767 ━━━━━━━━━━━━  } simultaneously!
// Thread 4: Rows 768-1023 ━━━━━━━━━━━━The Divide-and-Conquer Tree
//                    [Rows 0-1023]
//                    /            \
//           [Rows 0-511]      [Rows 512-1023]
//           /        \            /         \
//     [0-255]    [256-511]  [512-767]  [768-1023]
//     /    \       /    \      /    \      /     \
//   [0-127] [128-255] ...  (continues until threshold reached)Each box is a RecursiveTask that either:
//
// Splits into smaller tasks (if above threshold)
// Computes directly (if at or below threshold)


import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinMatrixMultiplier implements MatrixMultiplier {

    private final int threshold;
    private final ForkJoinPool pool;

    /**
     * Creates a parallel multiplier with specified threshold.
     *
     * @param threshold minimum number of rows to compute sequentially (stop splitting below this)
     */
    public ForkJoinMatrixMultiplier(int threshold) {
        if (threshold < 1) {
            throw new IllegalArgumentException("Threshold must be at least 1");
        }
        this.threshold = threshold;
        // Use common pool (size = number of CPU cores)
        this.pool = ForkJoinPool.commonPool();
    }

    @Override
    public Matrix multiply(Matrix a, Matrix b) {
        // Validation
        Objects.requireNonNull(a, "Matrix A cannot be null");
        Objects.requireNonNull(b, "Matrix B cannot be null");

        MatrixUtils.validateDimensions(a, b);

        // Get dimensions
        int m = a.getRows();
        int p = b.getCols();

        // Create result matrix (all threads will write to this)
        double[][] result = new double[m][p];

        // Create and execute the root task
        MultiplyTask task = new MultiplyTask(a, b, result, 0, m);
        pool.invoke(task);  // Execute and wait for completion (Blocks until the entire computation is done)

        return new Matrix(result);
    }

    /**
     * RecursiveAction that computes a range of rows in the result matrix.
     * <p>
     * Each task is responsible for computing rows [startRow, endRow).
     * If the range is too large, it splits into two subtasks.
     * If small enough (≤ threshold), it computes directly.
     */
    private class MultiplyTask extends RecursiveAction {

        private final Matrix a;
        private final Matrix b;
        private final double[][] result;  // Shared result array
        private final int startRow;       // Inclusive
        private final int endRow;         // Exclusive

        /**
         * Creates a task to compute rows [startRow, endRow).
         */
        public MultiplyTask(Matrix a, Matrix b, double[][] result,
                            int startRow, int endRow) {
            this.a = a;
            this.b = b;
            this.result = result;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        protected void compute() {
            int rowCount = endRow - startRow;

            // Base case: small enough to compute directly
            if (rowCount <= threshold) {
                computeDirectly();
            } else {
                // Recursive case: split into two subtasks
                int mid = startRow + rowCount / 2;

                MultiplyTask leftTask = new MultiplyTask(a, b, result, startRow, mid);
                MultiplyTask rightTask = new MultiplyTask(a, b, result, mid, endRow);

                // Fork left task (runs in parallel)
                leftTask.fork();
                // Compute right task in current thread
                rightTask.compute();
                // Wait for left task to complete
                leftTask.join();
            }
        }

        private void computeDirectly() {
            int n = a.getCols();
            int p = b.getCols();

            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < p; j++) {
                    double sum = 0.0;
                    for (int k = 0; k < n; k++) {
                        sum += a.get(i, k) * b.get(k, j);
                    }
                    result[i][j] = sum;
                }
            }
        }
    }
}