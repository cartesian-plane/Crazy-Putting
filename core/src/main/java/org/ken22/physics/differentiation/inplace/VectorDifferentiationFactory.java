package org.ken22.physics.differentiation.inplace;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.differentiators.Differentiator;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.utils.MathUtils;

import java.util.function.BiConsumer;

public class VectorDifferentiationFactory {
    private Differentiator differentiator;
    private double h;
    private boolean complete;

    private GolfCourse course;
    private Expression expr;

    private InPlaceVectorDifferentiation4 normalComplete;
    private InPlaceVectorDifferentiation4 lowComplete;
    private InPlaceVectorDifferentiation4 normalSimple;
    private InPlaceVectorDifferentiation4 lowSimple;

    public VectorDifferentiationFactory(double h, Expression expr, GolfCourse course, Differentiator differentiator, boolean completePhysics) {
        this.h = h;
        this.expr = expr;
        this.course = course;
        this.differentiator = differentiator;
        this.complete = completePhysics;

        this.normalComplete = new InPlaceVectorDifferentiation4() {
            private double g = course.gravitationalConstant();
            private double kf_g = course.kineticFrictionGrass();

            @Override
            public double[] apply(Double h, StateVector4 sv) {
                double[] der = new double[4];

                //acceleration
                double df_dx = xSlope(sv.x(), sv.y());
                double df_dy = ySlope(sv.x(), sv.y());
                double d_norm = MathUtils.magnitude(1, df_dx, df_dy);
                double big_term = MathUtils.magnitude(sv.vx(), sv.vx(), df_dx * sv.vx() + df_dy * sv.vy());
                double accx = -(this.g / d_norm) * (df_dx / d_norm + this.kf_g * sv.vx() / (big_term));
                double accy = -(this.g / d_norm) * (df_dy / d_norm + this.kf_g * sv.vy() / (big_term));

                // Approximation:
                der[0] = sv.x() + h * sv.vx();
                der[1] = sv.y() + h * sv.vy();
                der[2] = sv.vx() + h * accx;
                der[3] = sv.vy() + h * accy;

                // Derivative:
                df_dx = xSlope(der[0], der[1]);
                df_dy = ySlope(der[0], der[1]);
                d_norm = MathUtils.magnitude(1, df_dx, df_dy);
                big_term = MathUtils.magnitude(df_dx * der[2] + df_dy * der[3]);

                der[0] = der[2];
                der[1] = der[3];
                der[2] = -(this.g / d_norm) * (df_dx / d_norm + this.kf_g * der[2] / (big_term));
                der[3] = -(this.g / d_norm) * (df_dy / d_norm + this.kf_g * der[3] / (big_term));

                return der;
            }
        };

        this.lowComplete = new InPlaceVectorDifferentiation4() {
            private double g = course.gravitationalConstant();
            private double kf_g = course.kineticFrictionGrass();

            @Override
            public double[] apply(Double h, StateVector4 sv) {
                double[] der = new double[4];

                double vx = sv.vx();
                double vy = sv.vy();
                double df_dx = xSlope(vx, vy);
                double df_dy = ySlope(vx, vy);
                double d_norm = MathUtils.magnitude(1, df_dx, df_dy);
                // approximation not from booklet vx, vy -> dh_dx, dh_dy and vx^2, vy^2 -> 0
                double big_term = MathUtils.magnitude(df_dy * df_dx + df_dy * df_dy);
                //acceleration
                double accx = -(this.g * df_dx / d_norm) * (1/d_norm + this.kf_g / (d_norm * big_term));
                double accy = -(this.g * df_dy / d_norm) * (1/d_norm + this.kf_g / (d_norm * big_term));

                // Approximation:
                der[0] = sv.x() + h * sv.vx();
                der[1] = sv.y() + h * sv.vy();
                der[2] = sv.vx() + h * accx;
                der[3] = sv.vy() + h * accy;

                // Derivative:
                df_dx = xSlope(der[0], der[1]);
                df_dy = ySlope(der[0], der[1]);
                d_norm = MathUtils.magnitude(1, df_dx, df_dy);
                big_term = MathUtils.magnitude(df_dy * df_dx + df_dy * df_dy);
                der[0] = der[2];
                der[1] = der[3];
                der[2] = -(this.g * df_dx / d_norm) * (1/d_norm + this.kf_g / (d_norm * big_term));
                der[3] = -(this.g * df_dy / d_norm) * (1/d_norm + this.kf_g / (d_norm * big_term));

                return der;
            }
        };

        this.normalSimple = new InPlaceVectorDifferentiation4() {
            @Override
            public double[] apply(Double h, StateVector4 sv) {
                double[] der = new double[4];

                double df_dx = xSlope(sv.x(), sv.y());
                double df_dy = ySlope(sv.x(), sv.y());
                double v_norm = MathUtils.magnitude(sv.vx(), sv.vy());
                //acceleration
                double accx = -course.gravitationalConstant() * (df_dx + course.kineticFrictionGrass() * sv.vx() / v_norm);
                double accy = -course.gravitationalConstant() * (df_dy + course.kineticFrictionGrass() * sv.vy() / v_norm);

                // sv approximation at t + h // temporarily stored in der for efficiency
                der[0] = sv.x() + h * sv.vx();
                der[1] = sv.y() + h * sv.vy();
                der[2] = sv.vx() + h * accx;
                der[3] = sv.vy() + h * accy;

                // Derivative:
                der[0] = der[2];
                der[1] = der[3];
                df_dx = xSlope(der[0], der[1]);
                df_dy = ySlope(der[0], der[1]);
                v_norm = MathUtils.magnitude(der[2], der[3]);
                der[2] = -course.gravitationalConstant() * (df_dx + course.kineticFrictionGrass() * der[2] / v_norm);
                der[3] = -course.gravitationalConstant() * (df_dy + course.kineticFrictionGrass() * der[3] / v_norm);

                return der;
            }
        };

        this.lowSimple = new InPlaceVectorDifferentiation4() {
            @Override
            public double[] apply(Double h, StateVector4 sv) {
                double[] der = new double[4];

                double df_dx = xSlope(sv.x(), sv.y());
                double df_dy = ySlope(sv.x(), sv.y());
                double d_norm = MathUtils.magnitude(df_dx, df_dy);
                //acceleration
                double accx;
                double accy;
                if (d_norm == 0) {
                    accx = 0;
                    accy = 0;
                } else {
                    accx = -course.gravitationalConstant() * df_dx * (1 + course.kineticFrictionGrass() / d_norm);
                    accy = -course.gravitationalConstant() * df_dy * (1 + course.kineticFrictionGrass() / d_norm);
                }

                // approximation
                der[0] = sv.x() + h * sv.vx();
                der[1] = sv.y() + h * sv.vy();
                der[2] = sv.vx() + h * accx;
                der[3] = sv.vy() + h * accy;

                // Derivative:
                df_dx = xSlope(der[0], der[1]);
                df_dy = ySlope(der[0], der[1]);
                d_norm = MathUtils.magnitude(df_dx, df_dy);
                der[0] = der[2];
                der[1] = der[3];
                if (d_norm == 0) {
                    der[2] = 0;
                    der[3] = 0;
                } else {
                    der[2] = -course.gravitationalConstant() * df_dx * ( 1 + course.kineticFrictionGrass() / d_norm);
                    der[3] = -course.gravitationalConstant() * df_dy * ( 1 + course.kineticFrictionGrass() / d_norm);
                }

                return der;
            }
        };
    }

    public InPlaceVectorDifferentiation4 normalSpeedVectorDifferentiation4() {
        if (complete)
            return normalComplete;
        else
            return normalSimple;
    }

    public InPlaceVectorDifferentiation4 lowSpeedVectorDifferentiation4() {
        if (complete)
            return lowComplete;
        else
            return lowSimple;
    }

    public double xSlope(double x, double y) {
        return MathUtils.xSlope(x, y, h, expr, differentiator);
    }

    public double ySlope(double x, double y) {
        return MathUtils.ySlope(x, y, h, expr, differentiator);
    }
}
