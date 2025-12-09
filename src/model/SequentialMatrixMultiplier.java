package model;

public class SequentialMatrixMultiplier implements MatrixMultiplier {

    @Override
    public Matrix multiply(Matrix a, Matrix b) {
        MatrixUtils.validateDimensions(a, b);

        int m = a.getRows();
        int n = a.getCols();
        int p = b.getCols();

        double[][] result = new double[m][p];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += a.get(i, k) * b.get(k, j);
                }
                result[i][j] = sum;
            }
        }

        return new Matrix(result);
    }
}