package org.ken22.physicsx.differentiation;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physicsx.vectors.StateVector4;

import java.util.function.Function;

public class VectorDifferentiationFactory {
    private Differentiator differentiator;
    private double h;

    private GolfCourse course;
    private Expression expr;

    public VectorDifferentiationFactory(double h, Expression expr, GolfCourse course, Differentiator differentiator) {
        this.h = h;
        this.expr = expr;
        this.course = course;
        this.differentiator = differentiator;
    }

    private static Function<StateVector4, Double> dx = (stateVector4) -> stateVector4.vx();
    private static Function<StateVector4, Double> dy = (stateVector4) -> stateVector4.vy();

    public VectorDifferentiation4 vectorDifferentiation4(double xCoord, double yCoord) {
        // Define the univariate functions at the x and y coordinates
        Function<Double, Double> fx = (x) -> expr.setVariable("y", yCoord).setVariable("x", x).evaluate();
        Function<Double, Double> fy = (y) -> expr.setVariable("x", xCoord).setVariable("y", y).evaluate();

        // Aproximate the derivatives of the functions at the x and y coordinates
        double df_dx = differentiator.differentiate(h, xCoord, fx);
        double df_dy = differentiator.differentiate(h, yCoord, fy);

        // Define the velocity differentiation functions
        Function<StateVector4, Double> dvx = (stateVector4) ->
            (-course.gravitationalConstant() * df_dx - course.kineticFrictionGrass() * stateVector4.vx() /
                magnitude(stateVector4.vx(), stateVector4.vy()))/course.mass();
        Function<StateVector4, Double> dvy = (stateVector4) ->
            (-course.gravitationalConstant() * df_dy - course.kineticFrictionGrass() * stateVector4.vy() /
                magnitude(stateVector4.vx(), stateVector4.vy()))/course.mass();

        // Return the vector differentiation object
        return new VectorDifferentiation4(dx, dy, dvx, dvy);
    }

    public VectorDifferentiation4 lowSpeedVectorDifferentiation4(double xCoord, double yCoord) {
        // Define the univariate functions at the x and y coordinates
        Function<Double, Double> fx = (x) -> expr.setVariable("y", yCoord).setVariable("x", x).evaluate();
        Function<Double, Double> fy = (y) -> expr.setVariable("x", xCoord).setVariable("y", y).evaluate();

        // Aproximate the derivatives of the functions at the x and y coordinates
        double df_dx = differentiator.differentiate(h, xCoord, fx);
        double df_dy = differentiator.differentiate(h, yCoord, fy);

        // Define the velocity differentiation functions
        Function<StateVector4, Double> dvx = (stateVector4) ->
            (-course.gravitationalConstant() * df_dx - course.kineticFrictionGrass() * df_dx /
            magnitude(df_dx, df_dy))/course.mass();
        Function<StateVector4, Double> dvy = (stateVector4) ->
            (-course.gravitationalConstant() * df_dy - course.kineticFrictionGrass() * df_dy /
                magnitude(df_dx, df_dy))/course.mass();

        // Return the vector differentiation object
        return new VectorDifferentiation4(dx, dy, dvx, dvy);
    }

    private double magnitude(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }
}
