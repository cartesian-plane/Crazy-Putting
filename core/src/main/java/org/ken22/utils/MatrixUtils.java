package org.ken22.utils;

public final class MatrixUtils {
    public static double[][] multiply(double[][] a, double[][] b) {
        int aRows = a.length;
        int aCols = a[0].length;
        int bRows = b.length;
        int bCols = b[0].length;
        if (aCols != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aCols + " did not match B:Columns " + bRows + ".");
        }
        double[][] result = new double[aRows][bCols];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bCols; j++) {
                result[i][j] = 0.00000;
            }
        }
        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bCols; j++) { // bColumn
                for (int k = 0; k < aCols; k++) { // aColumn
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    public static double[][] inverse(double[][] a) {
        if (! (a[0].length == 2 && a.length == 2)) {
            throw new UnsupportedOperationException("Can only invert 2x2 matrix");
        }

        double k = 1 / (a[0][0] * a[1][1] - a[0][1] * a[1][0]);

        return new double[][] {
            {a[1][1] * k, -a[0][1] * k},
            {-a[0][1] * k, a[0][0] * k}
        };

    }
}
