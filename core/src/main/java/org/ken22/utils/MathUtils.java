package org.ken22.utils;

import net.objecthunter.exp4j.Expression;
import org.ken22.physics.differentiators.Differentiator;
import org.ken22.physics.vectors.StateVector4;

import java.util.function.Function;

/**
 * This class imitates some of the NumPy functionality for convenience.
 */
public class MathUtils {
    public static final double paddingSize = 2.5;
    public static String[] courses = {"0.4*(0.9-exp(-(x^2+y^2)/8))", "1.41421356237+sin(x)+cos(y)", "3*x-2*y", "0.5"};
    public static double mass = 1.0;
    public static double range = 5.0;
    public static double maxspeed = 5.0;
    public static double g = 9.80665;
    public static double kf_s = 0.3;
    public static double sf_s = 0.4;
    public static double targetRadius = 0.1;
    public static final double[] kineticFriction = {0.15, 0.05, 0.01};
    public static final double[] staticFriction = {0.1, 0.15, 0.2};
    public static final double[] velocities = {-5.0, -2, -0.0001, 0, 0.0001, 2, 5.0};
    public static final double[] velocitiesx = {0, 0.0001, 3.0};
    public static final double[] velocitiesy = {0, 0.0001, 4.0};
    public static final double[] stepsizes = {1/60.0, 1/(60.0*2), 1/(60.0*3)};

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

    /**
     * Returns the magnitude (L2 norm) of a given 2-d vector represented as an array of doubles.
     *
     * @param vals array of doubles
     * @return the magnitude (L2 norm) of the given vector
     */
    public static double magnitude(double... vals) {
        double sum = 0;
        for(double val : vals) {
            sum += val*val;
        }
        return Math.sqrt(sum);
    }

    public static double xSlope(double xCoord, double yCoord, double h,
                                Expression expr, Differentiator differentiator) {
        // Define the univariate functions at the x coordinate
        Function<Double, Double> fx = (x) -> expr.setVariable("y", yCoord).setVariable("x", x).evaluate();
        // Approximate the derivatives of the functions at the x coordinate
        return differentiator.differentiate(h, xCoord, fx);
    }

    public static double ySlope(double xCoord, double yCoord,
                                double h, Expression expr, Differentiator differentiator) {
        // Define the univariate functions at the y coordinate
        Function<Double, Double> fy = (y) -> expr.setVariable("x", xCoord).setVariable("y", y).evaluate();
        // Approximate the derivatives of the functions at the y coordinate
        //noinspection SuspiciousNameCombination
        return differentiator.differentiate(h, yCoord, fy);
    }

    public static double cross2D(double x1, double y1, double x2, double y2) {
        return x1 * y2 - x2 * y1;
    }

    // credit to https://stackoverflow.com/questions/217578/how-can-i-determine-whether-a-2d-point-is-within-a-polygon
    // Check if a point is inside a quadrilateral using the cross product method (credit to GPT the almighty)
    public static boolean pointInQuadrilateral(double px, double py, double smx, double smy, double spx, double spy, double epx, double epy, double emx, double emy) {
        double sign1 = cross2D(px - smx, py - smy, emx - smx, emy - smy);
        double sign2 = cross2D(px - emx, py - emy, epx - emx, epy - emy);
        double sign3 = cross2D(px - epx, py - epy, spx - epx, spy - epy);
        double sign4 = cross2D(px - spx, py - spy, smx - spx, smy - spy);

        boolean allPositive = (sign1 > 0 && sign2 > 0 && sign3 > 0 && sign4 > 0);
        boolean allNegative = (sign1 < 0 && sign2 < 0 && sign3 < 0 && sign4 < 0);

        return allPositive || allNegative;
    }

    public static double[] unitNormal2D(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        // Perpendicular vector (reciprocal)
        double var1 = -dy;
        double var2 = dx;

        // Normalize the normal vector
        double length = Math.sqrt(var1 * var1 + var2 * var2);
        return new double[]{var1 / length, var2 / length};
    }

    public static double dot2D(double x1, double y1, double x2, double y2) {
        return x1 * x2 + y1 * y2;
    }

    // Multiply a vector by a scalar
    public static double[] multiply(double[] vector, double scalar) {
        return new double[]{vector[0] * scalar, vector[1] * scalar};
    }

    // Calculate the reflected vector
    public static double[] reflectedVector2D(double xn, double yn, double vx, double vy) {
        double dot = dot2D(vx, vy, xn, yn);

        // Calculate the reflected vector
        return new double[]{vx - 2 * dot * xn, vy - 2 * dot * yn};
    }
}
