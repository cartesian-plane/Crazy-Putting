package org.ken22.utils;

public class MathUtils {
    public static double[] linspace(double start, double end, int num) {
        double[] result = new double[num];
        double step = (end - start) / (num - 1);

        for (int i = 0; i < num; i++) {
            result[i] = start + i * step;
        }

        return result;
    }

    public static double min(double[][] array) {
        double min = Double.MAX_VALUE;

        for (double[] row : array) {
            for (double value : row) {
                if (value < min) {
                    min = value;
                }
            }
        }

        return min;
    }

    public static double max(double[][] array) {
        double max = -Double.MAX_VALUE;

        for (double[] row : array) {
            for (double value : row) {
                if (value > max) {
                    max = value;
                }
            }
        }

        return max;
    }
}
