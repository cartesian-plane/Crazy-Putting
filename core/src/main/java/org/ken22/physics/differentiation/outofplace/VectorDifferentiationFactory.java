package org.ken22.physics.differentiation.outofplace;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.InjectedClass;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.differentiators.Differentiator;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.utils.MathUtils;

public class VectorDifferentiationFactory {
    private Differentiator differentiator;
    private double h;

    private InjectedClass expr;

    InstVecDiffFactory instDiffFact;

    private VectorDifferentiation4 normalSpeed;
    private VectorDifferentiation4 lowSpeed;

    public VectorDifferentiationFactory(double h, InjectedClass expr, GolfCourse course, Differentiator differentiator,
                                        boolean completePhysics) {
        this.h = h;
        this.expr = expr;
        this.differentiator = differentiator;
        if(completePhysics) {
            instDiffFact = new InstVecDiffFactoryComplete(h, expr, course, differentiator);
        } else {
            instDiffFact = new InstantaneousVectorDifferentiationFactory(h, expr, course, differentiator);
        }

        this.normalSpeed = new VectorDifferentiation4() {
            private InstantaneousVectorDifferentiation4 instDiff = instDiffFact.instantaneousVectorDifferentiation4();
            @Override
            public StateVector4 apply(Double h1, StateVector4 sv) {
                StateVector4 dsv = instDiff.apply(sv); //derivative of sv
                StateVector4 svh = sv.add(dsv.multiply(h1)); //approximation of sv at t + h
                StateVector4 dsvh = instDiff.apply(svh); //derivative of approximation
                return dsvh;
            }
        };

        this.lowSpeed = new VectorDifferentiation4() {
            private InstantaneousVectorDifferentiation4 instDiff = instDiffFact.altInstantaneousVectorDifferentiation4();
            @Override
            public StateVector4 apply(Double h1, StateVector4 sv) {
                StateVector4 dsv = instDiff.apply(sv);
                StateVector4 svh = sv.add(dsv.multiply(h1));
                StateVector4 dsvh = instDiff.apply(svh);
                return dsvh;
            }
        };
    }

    public VectorDifferentiation4 normalSpeedVectorDifferentiation4() {
        return normalSpeed;
    }

    public VectorDifferentiation4 lowSpeedVectorDifferentiation4() {
        return lowSpeed;
    }

    public double xSlope(double x, double y) {
        return MathUtils.xSlope(x, y, h, expr, differentiator);
    }

    public double ySlope(double x, double y) {
        return MathUtils.ySlope(x, y, h, expr, differentiator);
    }
}
