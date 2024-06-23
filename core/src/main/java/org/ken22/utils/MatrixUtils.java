package org.ken22.utils;

public final class MatrixUtils {
    public static double[] transform(double[][] a, double[] b) {
        return new double[] {b[0] * a[0][0] + b[1] * a[0][1], b[0] * a[1][0] + b[1] * a[1][1]};

    }

    public static double[][] inverse(double[][] a) {
        if (! (a[0].length == 2 && a.length == 2)) {
            throw new UnsupportedOperationException("Can only invert 2x2 matrix");
        }

        double k = 1 / (a[0][0] * a[1][1] - a[0][1] * a[1][0]);

        return new double[][] {
            {a[1][1] * k, -a[0][1] * k},
            {-a[1][0] * k, a[0][0] * k}
        };

    }
}
