package org.ken22.physicsx.differentiation;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physicsx.utils.PhysicsUtils;
import org.ken22.physicsx.vectors.StateVector4;

import java.util.function.Function;

public class VectorDifferentiationFactory {
    private final Differentiator differentiator;
    private final double h;

    private final GolfCourse course;
    private final Expression expr;

    public VectorDifferentiationFactory(double h, Expression expr, GolfCourse course, Differentiator differentiator) {
        this.h = h;
        this.expr = expr;
        this.course = course;
        this.differentiator = differentiator;
    }

    @SuppressWarnings("Convert2MethodRef")
    private static final Function<StateVector4, Double> dx = (stateVector4) -> stateVector4.vx();
    @SuppressWarnings("Convert2MethodRef")
    private static final Function<StateVector4, Double> dy = (stateVector4) -> stateVector4.vy();

    public VectorDifferentiation4 normalSpeedVectorDifferentiation4(double xCoord, double yCoord) {

        double df_dx = PhysicsUtils.xSlope(xCoord, yCoord, h, expr, differentiator);
        double df_dy = PhysicsUtils.ySlope(xCoord, yCoord, h, expr, differentiator);

        // Define the velocity differentiation functions
        Function<StateVector4, Double> dvx = (stateVector4) ->
            (-course.gravitationalConstant() * df_dx - course.gravitationalConstant() * course.kineticFrictionGrass() * stateVector4.vx() /
                PhysicsUtils.magnitude(stateVector4.vx(), stateVector4.vy()))/course.mass();
        Function<StateVector4, Double> dvy = (stateVector4) ->
            (-course.gravitationalConstant() * df_dy - course.gravitationalConstant() * course.kineticFrictionGrass() * stateVector4.vy() /
                PhysicsUtils.magnitude(stateVector4.vx(), stateVector4.vy()))/course.mass();

        // Return the vector differentiation object
        return new VectorDifferentiation4(dx, dy, dvx, dvy);
    }

    public VectorDifferentiation4 lowSpeedVectorDifferentiation4(double xCoord, double yCoord) {

        double df_dx = PhysicsUtils.xSlope(xCoord, yCoord, h, expr, differentiator);
        double df_dy = PhysicsUtils.ySlope(xCoord, yCoord, h, expr, differentiator);

        // Define the velocity differentiation functions
        Function<StateVector4, Double> dvx = (stateVector4) ->
            (-course.gravitationalConstant() * df_dx - course.gravitationalConstant() * course.kineticFrictionGrass() * df_dx /
            PhysicsUtils.magnitude(df_dx, df_dy))/course.mass();
        Function<StateVector4, Double> dvy = (stateVector4) ->
            (-course.gravitationalConstant() * df_dy - course.gravitationalConstant() * course.kineticFrictionGrass() * df_dy /
                PhysicsUtils.magnitude(df_dx, df_dy))/course.mass();

        // Return the vector differentiation object
        return new VectorDifferentiation4(dx, dy, dvx, dvy);
    }
}
