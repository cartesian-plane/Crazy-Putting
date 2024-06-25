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

    // credit to https://stackoverflow.com/questions/217578/how-can-i-determine-whether-a-2d-point-is-within-a-polygon
    // and to chatgpt for using the crossproduct idea
    public static boolean pointInQuadrilateral(double px, double py, double qx1, double qy1, double qx2, double qy2, double qx3, double qy3, double qx4, double qy4) {
        return sameSide(px, py, qx1, qy1, qx2, qy2, qx3, qy3) &&
            sameSide(px, py, qx2, qy2, qx3, qy3, qx4, qy4) &&
            sameSide(px, py, qx3, qy3, qx4, qy4, qx1, qy1) &&
            sameSide(px, py, qx4, qy4, qx1, qy1, qx2, qy2);
    }

//    public static double[] closestEdge(double x1, double x2, double x3, double qy1, double qx2, double qy2, double qx3, double qy3, double qx4, double qy4) {
//        double[] closest = {qx1, qy1, qx2, qy2};
//        double minDist = Double.MAX_VALUE;
//        double[] edge1 = {qx1, qy1, qx2, qy2};
//        double[] edge2 = {qx2, qy2, qx3, qy3};
//        double[] edge3 = {qx3, qy3, qx4, qy4};
//        double[] edge4 = {qx4, qy4, qx1, qy1};
//        double[][] edges = {edge1, edge2, edge3, edge4};
//        for (double[] edge : edges) {
//            double[] closestEdge = closestEdge(px, py, edge[0], edge[1], edge[2], edge[3]);
//            double dist = MathUtils.magnitude(px - closestEdge[0], py - closestEdge[1]);
//            if (dist < minDist) {
//                minDist = dist;
//                closest = closestEdge;
//            }
//        }
//        return closest;
//    }

    public static boolean sameSide(double px1, double py1, double px2, double py2, double lx1, double ly1, double lx2, double ly2) {
        double dx1 = px1 - lx1;
        double dy1 = py1 - ly1;
        double dx2 = px2 - lx1;
        double dy2 = py2 - ly1;

        // Cross product
        double c1 = dx1 * dy2 - dx2 * dy1;
        double c2 = dx2 * dy2 - dx2 * dy1;

        return c1 * c2 >= 0;
    }

    public static double cross2D(StateVector4 vec1, StateVector4 vec2) {
        return vec1.x() * vec2.y() - vec1.y() * vec2.x();
    }
}
