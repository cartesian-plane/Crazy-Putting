package org.ken22.physics.differentiation.outofplace;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.differentiators.Differentiator;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.utils.MathUtils;

public class VectorDifferentiationFactory {
    private Differentiator differentiator;
    private double h;

    private Expression expr;

    InstVecDiffFactory instDiffFact;

    public VectorDifferentiationFactory(double h, Expression expr, GolfCourse course, Differentiator differentiator, boolean completePhysics) {
        this.h = h;
        this.expr = expr;
        this.differentiator = differentiator;
        if(completePhysics) {
            instDiffFact = new InstVecDiffFactoryComplete(h, expr, course, differentiator);
        } else {
            instDiffFact = new InstantaneousVectorDifferentiationFactory(h, expr, course, differentiator);
        }
    }

    public VectorDifferentiation4 normalSpeedVectorDifferentiation4() {
        InstantaneousVectorDifferentiation4 instDiff = instDiffFact.instantaneousVectorDifferentiation4();
        return (h1, sv) -> {
            StateVector4 dsv = instDiff.apply(sv);
            StateVector4 svh = sv.add(dsv.multiply(h1));
            StateVector4 dsvh = instDiff.apply(svh);
            return dsvh;
        };
    }

    public VectorDifferentiation4 lowSpeedVectorDifferentiation4() {
        InstantaneousVectorDifferentiation4 instDiff = instDiffFact.altInstantaneousVectorDifferentiation4();
        return (h1, sv) -> {
            StateVector4 dsv = instDiff.apply(sv);
            StateVector4 svh = sv.add(dsv.multiply(h1));
            StateVector4 dsvh = instDiff.apply(svh);
            return dsvh;
        };
    }

    public double xSlope(double x, double y) {
        return MathUtils.xSlope(x, y, h, expr, differentiator);
    }

    public double ySlope(double x, double y) {
        return MathUtils.ySlope(x, y, h, expr, differentiator);
    }
}
