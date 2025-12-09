package model;

public class Matrix {
    private final double[][] data;
    private final int rows;
    private final int cols;

    public Matrix(double[][] data) {
        if (data == null || data.length == 0 || data[0].length == 0) {
            throw new IllegalArgumentException("model.Matrix cannot be null or empty.");
        }

        // Check for jagged arrays
        int expectedCols = data[0].length;
        for (int i = 1; i < data.length; i++) {
            if (data[i].length != expectedCols) {
                throw new IllegalArgumentException(
                        "model.Matrix must be rectangular!"
                );
            }
        }

        this.rows = data.length;
        this.cols = data[0].length;
        this.data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, this.data[i], 0, cols);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public double get(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException(
                    String.format("Invalid indices (%d, %d) for matrix of size %dx%d",
                            row, col, rows, cols)
            );
        }
        return data[row][col];
    }
}