package org.ken22.utils;

/**
 * This class imitates some of the NumPy functionality for convenience.
 */
public class MathUtils {
    /**
     * Return evenly spaced numbers over a specified interval.
     * @param start The starting value of the sequence.
     * @param end The end value of the sequence.
     * @param num Number of samples to generate.
     * @return <i>num</i> equally spaced samples in the closed interval [start, end]
     */
    public static double[] linspace(double start, double end, int num) {
        double[] result = new double[num];
        double step = (end - start) / (num - 1);

        for (int i = 0; i < num; i++) {
            result[i] = start + i * step;
        }

        return result;
    }

    /**
     * Return the minimum of an array.
     *
     * @param array input array
     * @return minimum element of array
     */
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

    /**
     * Return the maximum of an array.
     *
     * @param array input array
     * @return maximum element of array
     */
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
