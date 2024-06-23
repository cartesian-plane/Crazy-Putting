package org.ken22.physics.utils;

import net.objecthunter.exp4j.Expression;
import org.ken22.physics.differentiators.Differentiator;

import java.util.function.Function;

public class PhysicsUtils {
    public static double magnitude(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Returns the magnitude (L2 norm) of a given 2-d vector represented as an array of doubles.
     *
     * @param vector array of doubles
     * @return the magnitude (L2 norm) of the given vector
     */
    public static double magnitude(double[] vector) {
        return magnitude(vector[0], vector[1]);
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
}
