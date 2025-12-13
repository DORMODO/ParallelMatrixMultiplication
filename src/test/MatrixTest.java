package test;

import model.ForkJoinMatrixMultiplier;
import model.Matrix;
import model.MatrixUtils;
import model.SequentialMatrixMultiplier;

public class MatrixTest {

    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("MATRIX MULTIPLICATION TESTS");
        System.out.println("=".repeat(60));

        int passed = 0;
        int total = 3;

        // Test 1: Sequential == Parallel
        System.out.print("Test 1 - Sequential vs Parallel (100x100)... ");
        Matrix A = MatrixUtils.randomMatrix(100, 100);
        Matrix B = MatrixUtils.randomMatrix(100, 100);
        Matrix seqResult = new SequentialMatrixMultiplier().multiply(A, B);
        Matrix parResult = new ForkJoinMatrixMultiplier(32).multiply(A, B);

        if (matricesEqual(seqResult, parResult)) {
            System.out.println("PASS");
            passed++;
        } else {
            System.out.println("FAIL");
        }

        // Test 2: Row × Column (dot product)
        System.out.print("Test 2 - Edge case: Row × Column... ");
        Matrix row = new Matrix(new double[][]{{1, 2, 3}});
        Matrix col = new Matrix(new double[][]{{4}, {5}, {6}});
        Matrix dotResult = new ForkJoinMatrixMultiplier(1).multiply(row, col);
        // 1*4 + 2*5 + 3*6 = 32
        if (Math.abs(dotResult.get(0, 0) - 32.0) < 1e-9) {
            System.out.println("PASS");
            passed++;
        } else {
            System.out.println("FAIL");
        }

        // Test 3: Dimension validation
        System.out.print("Test 3 - Dimension validation... ");
        try {
            Matrix incompatible = new Matrix(new double[][]{{1, 2}});
            new SequentialMatrixMultiplier().multiply(row, incompatible);
            System.out.println("FAIL (no exception thrown)");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS");
            passed++;
        }

        System.out.println("=".repeat(60));
        System.out.println("RESULT: " + passed + "/" + total + " tests passed");

        if (passed == total) {
            System.out.println("STATUS: ALL TESTS PASSED - Ready for benchmarking");
        } else {
            System.out.println("STATUS: SOME TESTS FAILED - Fix before benchmarking");
        }
        System.out.println("=".repeat(60) + "\n");
    }

    private static boolean matricesEqual(Matrix a, Matrix b) {
        if (a.getRows() != b.getRows() || a.getCols() != b.getCols()) {
            return false;
        }
        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getCols(); j++) {
                if (Math.abs(a.get(i, j) - b.get(i, j)) > 1e-9) {
                    return false;
                }
            }
        }
        return true;
    }
}