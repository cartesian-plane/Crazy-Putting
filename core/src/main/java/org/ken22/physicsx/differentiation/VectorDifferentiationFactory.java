package org.ken22.physicsx.differentiation;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physicsx.differentiators.Differentiator;
import org.ken22.physicsx.utils.PhysicsUtils;
import org.ken22.physicsx.vectors.StateVector4;

import java.util.function.BiFunction;
import java.util.function.Function;

public class VectorDifferentiationFactory {
    private Differentiator differentiator;
    private double h;

    private GolfCourse course;
    private Expression expr;

    InstantaneousVectorDifferentiationFactory instDiffFact;

    public VectorDifferentiationFactory(double h, Expression expr, GolfCourse course, Differentiator differentiator) {
        this.h = h;
        this.expr = expr;
        this.course = course;
        this.differentiator = differentiator;
        instDiffFact= new InstantaneousVectorDifferentiationFactory(h, expr, course, differentiator);
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
        return PhysicsUtils.xSlope(x, y, h, expr, differentiator);
    }

    public double ySlope(double x, double y) {
        return PhysicsUtils.ySlope(x, y, h, expr, differentiator);
    }
}
