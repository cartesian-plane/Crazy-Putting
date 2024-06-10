package org.ken22.physics.differentiation;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.differentiators.Differentiator;
import org.ken22.physics.utils.PhysicsUtils;
import org.ken22.physics.vectors.StateVector4;

public class InstantaneousVectorDifferentiationFactory {
    private Differentiator differentiator;
    private double h;

    private GolfCourse course;
    private Expression expr;

    public InstantaneousVectorDifferentiationFactory(double h, Expression expr, GolfCourse course, Differentiator differentiator) {
        this.h = h;
        this.expr = expr;
        this.course = course;
        this.differentiator = differentiator;
    }

    public InstantaneousVectorDifferentiation4 instantaneousVectorDifferentiation4() {
        return (sv) -> {
            double df_dx = xSlope(sv.x(), sv.y());
            double df_dy = ySlope(sv.x(), sv.y());
            double v_norm = PhysicsUtils.magnitude(sv.vx(), sv.vy());
            return new StateVector4(
                sv.vx(),
                sv.vy(),
                (-course.gravitationalConstant() * (df_dx + course.kineticFrictionGrass() * sv.vx() / v_norm)),
                (-course.gravitationalConstant() * (df_dy + course.kineticFrictionGrass() * sv.vy() / v_norm))
            );
        };
    }
    public InstantaneousVectorDifferentiation4 altInstantaneousVectorDifferentiation4() {
        return (sv) -> {
            double df_dx = xSlope(sv.x(), sv.y());
            double df_dy = ySlope(sv.x(), sv.y());
            double d_norm = PhysicsUtils.magnitude(df_dx, df_dy);
            return new StateVector4(
                sv.vx(),
                sv.vy(),
                (-course.gravitationalConstant() * (df_dx + course.kineticFrictionGrass() * df_dx / d_norm)),
                (-course.gravitationalConstant() * (df_dy + course.kineticFrictionGrass() * df_dy / d_norm))
            );
        };
    }

    public double xSlope(double x, double y) {
        return PhysicsUtils.xSlope(x, y, h, expr, differentiator);
    }

    public double ySlope(double x, double y) {
        return PhysicsUtils.ySlope(x, y, h, expr, differentiator);
    }

//    private static final Function<StateVector4, Double> dx = (stateVector4) -> stateVector4.vx();
//    private static final Function<StateVector4, Double> dy = (stateVector4) -> stateVector4.vy();
//    // Define the velocity differentiation functions
//    Function<StateVector4, Double> inst_dvx = (stateVector4) -> {
//        double df_dx = xSlope(stateVector4.x(), stateVector4.y());
//        return (-course.gravitationalConstant() * (df_dx + course.kineticFrictionGrass() * stateVector4.vx() /
//            PhysicsUtils.magnitude(stateVector4.vx(), stateVector4.vy())));
//    };
//    Function<StateVector4, Double> inst_dvy = (stateVector4) -> {
//        double df_dy = ySlope(stateVector4.x(), stateVector4.y());
//        return(-course.gravitationalConstant() * (df_dy + course.kineticFrictionGrass() * stateVector4.vy()/
//            PhysicsUtils.magnitude(stateVector4.vx(), stateVector4.vy())));
//    };
//    Function<StateVector4, Double> inst_alt_dvx = (stateVector4) -> {
//        double df_dx = xSlope(stateVector4.x(), stateVector4.y());
//        double df_dy = ySlope(stateVector4.x(), stateVector4.y());
//        return (-course.gravitationalConstant() * (df_dx + course.kineticFrictionGrass() * df_dx /
//            PhysicsUtils.magnitude(df_dx, df_dy)));
//    };
//    Function<StateVector4, Double> inst_alt_dvy = (stateVector4) -> {
//        double df_dy = ySlope(stateVector4.x(), stateVector4.y());
//        double df_dx = xSlope(stateVector4.x(), stateVector4.y());
//        return (-course.gravitationalConstant() * (df_dy + course.kineticFrictionGrass() * df_dy /
//            PhysicsUtils.magnitude(df_dx, df_dy)));
//    };
}
