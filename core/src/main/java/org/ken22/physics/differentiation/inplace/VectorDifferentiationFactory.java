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

    private Expression expr;

    public VectorDifferentiationFactory(double h, Expression expr, GolfCourse course, Differentiator differentiator, boolean completePhysics) {

    }

    public InPlaceVectorDifferentiation4 normalSpeedVectorDifferentiation4() {
        InPlaceVectorDifferentiation4 runStep;
        if (complete)
            runStep = (h, sv) -> {};
        else
            runStep = (h, sv) -> {};
        return runStep;
    }

    public InPlaceVectorDifferentiation4 lowSpeedVectorDifferentiation4() {
        InPlaceVectorDifferentiation4 runStep;
        if (complete)
            runStep = (h, sv) -> {};
        else
            runStep = (h, sv) -> {};
        return runStep;
    }

    public double xSlope(double x, double y) {
        return MathUtils.xSlope(x, y, h, expr, differentiator);
    }

    public double ySlope(double x, double y) {
        return MathUtils.ySlope(x, y, h, expr, differentiator);
    }
}
