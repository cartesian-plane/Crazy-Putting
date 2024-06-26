package org.ken22.physics.differentiation.outofplace;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.InjectedClass;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.differentiators.Differentiator;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.utils.MathUtils;

/**
 * Generate and return lambda functions of the given ODE system
 */
public class InstVecDiffFactoryComplete implements InstVecDiffFactory {

    private Differentiator differentiator;
    private double h;
    private InjectedClass expr;

    private InstantaneousVectorDifferentiation4 normalSpeed;
    private InstantaneousVectorDifferentiation4 lowSpeed;

    public InstVecDiffFactoryComplete(double h, InjectedClass expr, GolfCourse course, Differentiator differentiator) {
        this.h = h;
        this.expr = expr;
        this.differentiator = differentiator;

this.normalSpeed = new InstantaneousVectorDifferentiation4() {
            private double g = course.gravitationalConstant();
            private double kf_g = course.kineticFrictionGrass();

            @Override
            public StateVector4 apply(StateVector4 sv) {
                double vx = sv.vx();
                double vy = sv.vy();
                double x = sv.x();
                double y = sv.y();
                double df_dx = xSlope(x, y);
                double df_dy = ySlope(x, y);
                double d_norm = MathUtils.magnitude(1, df_dx, df_dy);
                double big_term = MathUtils.magnitude(vx, vy, df_dx*vx+df_dy*vy );

                return new StateVector4(
                    vx,
                    vy,
                    (-(this.g/d_norm) * (df_dx/d_norm + this.kf_g * vx / (big_term))),
                    (-(this.g/d_norm) * (df_dy/d_norm + this.kf_g * vy / (big_term)))
                );
            }
        };

        this.lowSpeed = new InstantaneousVectorDifferentiation4() {
            private double g = course.gravitationalConstant();
            private double kf_g = course.kineticFrictionGrass();
            @Override
            public StateVector4 apply(StateVector4 sv) {
                {
                    double x = sv.x();
                    double y = sv.y();
                    double vx = sv.vx();
                    double vy = sv.vy();
                    double df_dx = xSlope(x, y);
                    double df_dy = ySlope(x, y);
                    double d_norm = MathUtils.magnitude(1, df_dx, df_dy);

                    // approximation not from booklet vx, vy -> dh_dx, dh_dy and vx^2, vy^2 -> 0
                    double big_term = MathUtils.magnitude(df_dy*df_dx+df_dy*df_dy );

                    return new StateVector4(
                        vx,
                        vy,
                        (-(this.g*df_dx/d_norm) * (1/d_norm + this.kf_g / (big_term))),
                        (-(this.g*df_dy/d_norm) * (1/d_norm + this.kf_g / (big_term)))
                    );
                }
            }
        };
    }

    /**
     * Calculate velocity at current time and velocity of the next time step for normal speed
     * @return a lambda function that solves the ODE for normal speed
     */
    public InstantaneousVectorDifferentiation4 instantaneousVectorDifferentiation4() {
        return normalSpeed;
    }

    /**
     * Calculate velocity at current time and velocity of the next time step for low speed
     * @return a lambda function that solves the ODE for normal speed
     */
    public InstantaneousVectorDifferentiation4 altInstantaneousVectorDifferentiation4() {
        return lowSpeed;
    }

    /**
     * Calculate the slope of the x component of the expression
     * @param x: x coordinate
     * @param y: y coordinate
     * @return gradient of terrain along x-direction
     */
    public double xSlope(double x, double y) {
        return MathUtils.xSlope(x, y, h, expr, differentiator);
    }

    /**
     * Calculate the slope of the x component of the expression
     * @param x: x coordinate
     * @param y: y coordinate
     * @return gradient of terrain along x-direction
     */
    public double ySlope(double x, double y) {
        return MathUtils.ySlope(x, y, h, expr, differentiator);
    }

//    private static final Function<StateVector4, Double> dx = (stateVector4) -> stateVector4.vx();
//    private static final Function<StateVector4, Double> dy = (stateVector4) -> stateVector4.vy();
//    // Define the velocity differentiation functions
//    Function<StateVector4, Double> inst_dvx = (stateVector4) -> {
//        double df_dx = xSlope(stateVector4.x(), stateVector4.y());
//        return (-course.gravitationalConstant() * (df_dx + course.kineticFrictionGrass() * stateVector4.vx() /
//            MathUtils.magnitude(stateVector4.vx(), stateVector4.vy())));
//    };
//    Function<StateVector4, Double> inst_dvy = (stateVector4) -> {
//        double df_dy = ySlope(stateVector4.x(), stateVector4.y());
//        return(-course.gravitationalConstant() * (df_dy + course.kineticFrictionGrass() * stateVector4.vy()/
//            MathUtils.magnitude(stateVector4.vx(), stateVector4.vy())));
//    };
//    Function<StateVector4, Double> inst_alt_dvx = (stateVector4) -> {
//        double df_dx = xSlope(stateVector4.x(), stateVector4.y());
//        double df_dy = ySlope(stateVector4.x(), stateVector4.y());
//        return (-course.gravitationalConstant() * (df_dx + course.kineticFrictionGrass() * df_dx /
//            MathUtils.magnitude(df_dx, df_dy)));
//    };
//    Function<StateVector4, Double> inst_alt_dvy = (stateVector4) -> {
//        double df_dy = ySlope(stateVector4.x(), stateVector4.y());
//        double df_dx = xSlope(stateVector4.x(), stateVector4.y());
//        return (-course.gravitationalConstant() * (df_dy + course.kineticFrictionGrass() * df_dy /
//            MathUtils.magnitude(df_dx, df_dy)));
//    };
}
