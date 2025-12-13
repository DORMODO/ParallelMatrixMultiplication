package model;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

public class ForkJoinMatrixMultiplier implements MatrixMultiplier {

    private final int threshold;
    private final ForkJoinPool pool;

    public ForkJoinMatrixMultiplier(int threshold) {
        this.threshold = threshold;
        this.pool = ForkJoinPool.commonPool();
    }

    @Override
    public Matrix multiply(Matrix a, Matrix b) {
        return multiply(a, b, null);
    }

    public Matrix multiply(Matrix a, Matrix b, IntConsumer progressCallback) {
        MatrixUtils.validateDimensions(a, b);

        // Get dimensions
        int m = a.getRows();
        int p = b.getCols();

        // Create result matrix (all threads will write to this)
        double[][] result = new double[m][p];

        // Shared counter for completed rows (Is atomic to ensure thread-safety)
        AtomicInteger completedRows = new AtomicInteger(0);

        // Create and execute the root task
        MultiplyTask task = new MultiplyTask(a, b, result, 0, m, completedRows, progressCallback);
        // invoke() method from ForkJoinPool to start the task and blocks until finished.
        pool.invoke(task); // موجوده في أخر شرائح شرحناها

        return new Matrix(result);
    }


    // RecursiveAction is used for tasks that do not return a result (مثل ضرب المصفوفات هنا)
    private class MultiplyTask extends RecursiveAction {

        private final Matrix a;
        private final Matrix b;
        private final double[][] result;  // Shared result array
        private final int startRow;       // Inclusive (المعيد قال عليها)
        private final int endRow;         // Exclusive (المعيد قال عليها)
        private final AtomicInteger completedRows; // Shared counter across tasks
        private final IntConsumer progressCallback; // may be null

        public MultiplyTask(Matrix a, Matrix b, double[][] result,
                            int startRow, int endRow,
                            AtomicInteger completedRows,
                            IntConsumer progressCallback) {
            this.a = a;
            this.b = b;
            this.result = result;
            this.startRow = startRow;
            this.endRow = endRow;
            this.completedRows = completedRows;
            this.progressCallback = progressCallback;
        }

        // compute() method is where the task's logic is implemented, it overrides the abstract method from RecursiveAction.
        // This method decides whether to compute directly or split the task further.
        @Override
        protected void compute() {
            int rowCount = endRow - startRow;

            if (rowCount <= threshold) {
                computeDirectly();
            } else {
                int mid = startRow + rowCount / 2;

                MultiplyTask leftTask = new MultiplyTask(a, b, result, startRow, mid, completedRows, progressCallback);
                MultiplyTask rightTask = new MultiplyTask(a, b, result, mid, endRow, completedRows, progressCallback);

                leftTask.fork();
                rightTask.compute();
                leftTask.join();
            }
        }

        // Direct computation of the assigned rows without further splitting.
        // عملها هو ضرب المصفوفات العادي
        // بعد ما يخلص كل صف بيزيد العداد وبيبلغ ال progressCallback لو مش null
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

                int completed = completedRows.incrementAndGet();
                if (progressCallback != null) {
                    try {
                        progressCallback.accept(completed);
                    } catch (Exception _) {
                    }
                }
            }
        }
    }
}