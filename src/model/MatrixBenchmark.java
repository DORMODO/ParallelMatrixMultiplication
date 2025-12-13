package model;

public class MatrixBenchmark {
      public static void runThresholdTuning(int size, int[] thresholds) {
        System.out.println("\n=== Threshold Tuning for model.Matrix " + size + "x" + size + " ===");
        System.out.printf("%-10s %-15s %-10s\n", "Threshold", "Parallel Time(ms)", "Speedup");

        Matrix A = MatrixUtils.randomMatrix(size, size);
        Matrix B = MatrixUtils.randomMatrix(size, size);

        MatrixMultiplier seqMul = new SequentialMatrixMultiplier();

        long seqTime = MatrixUtils.measure(() -> seqMul.multiply(A, B));

        for (int threshold : thresholds) {
            MatrixMultiplier parMul = new ForkJoinMatrixMultiplier(threshold);

            long parTime = MatrixUtils.measure(() -> parMul.multiply(A, B));
            double speedup = (double) seqTime / parTime;

            System.out.printf("%-10d %-15d %-10.2f\n", threshold, parTime, speedup);
        }
        System.out.println("=== End of Threshold Tuning ===\n");
    }

    public static void main(String[] args) {
        int[] thresholds = {16, 32, 64, 128, 256, 512};
        int[] sizes = {256, 512, 1024};

        for (int size : sizes) {
            runThresholdTuning(size, thresholds);
        }
    }

}
  
