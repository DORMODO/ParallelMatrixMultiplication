package model;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MatrixUtils {

    private static final Random random = new Random();

    public static Matrix randomMatrix(int rows, int cols) {
        double[][] data = new double[rows][cols];

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                data[i][j] = random.nextDouble();

        return new Matrix(data);
    }

    public static void validateDimensions(Matrix a, Matrix b) {
        if (a.getCols() != b.getRows()) {
            throw new IllegalArgumentException(
                    String.format(
                            "Cannot multiply matrices: A is %d×%d but B is %d×%d. " +
                                    "A's column count (%d) must equal B's row count (%d).",
                            a.getRows(), a.getCols(), b.getRows(), b.getCols(),
                            a.getCols(), b.getRows()
                    )
            );
        }
    }

//    public static long measure(Runnable task) {
//        // Warmup: (أتكلمت عليه مع المعيد)
//        task.run();
//
//        int iterations = 5;
//        long totalTime = 0;
//        for (int i = 0; i < iterations; i++) {
//            long start = System.nanoTime();
//            task.run();
//            long end = System.nanoTime();
//            totalTime += (end - start);
//        }
//
//        return TimeUnit.NANOSECONDS.toMillis(totalTime / iterations);
//    }

    public static long measure(Runnable task) {
        long start = System.nanoTime();
        task.run();
        long end = System.nanoTime();
        return TimeUnit.NANOSECONDS.toMillis(end - start);
    }
}