package org.ken22.physics.odesolvers;

import net.objecthunter.exp4j.Expression;
import org.junit.jupiter.api.Test;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.TestUtils;
import org.ken22.physics.differentiation.outofplace.InstantaneousVectorDifferentiation4;
import org.ken22.physics.differentiation.outofplace.InstantaneousVectorDifferentiationFactory;
import org.ken22.physics.differentiation.outofplace.VectorDifferentiation4;
import org.ken22.physics.differentiation.outofplace.VectorDifferentiationFactory;
import org.ken22.physics.differentiators.FivePointCenteredDifference;
import org.ken22.physics.vectors.StateVector4;

import static org.junit.jupiter.api.Assertions.*;

class RK4Test {

    @Test
    void nextStep() {
        GolfCourse course = TestUtils.course("golf-course.json");
        Expression expr = TestUtils.expr(course);

        InstantaneousVectorDifferentiationFactory instDiffFact =
            new InstantaneousVectorDifferentiationFactory(0.00001, expr, course, new FivePointCenteredDifference());
        InstantaneousVectorDifferentiation4 instDiff = instDiffFact.instantaneousVectorDifferentiation4();

        VectorDifferentiationFactory factory =
            new VectorDifferentiationFactory(0.00001, expr, course, new FivePointCenteredDifference(), false);
        VectorDifferentiation4 vd = factory.normalSpeedVectorDifferentiation4();

        StateVector4 initial = new StateVector4(4.0, 4.0, 1.0, 1.0);
        StateVector4 result = vd.apply(0.00001, initial);

        StateVector4 initialDiff = instDiff.apply(initial);
        StateVector4 initialPlusDiff = initial.add(initialDiff.multiply(0.00001));
        StateVector4 resultDiff = instDiff.apply(initialPlusDiff);
        StateVector4 resultDiff2 = instDiff.apply(result);

        System.out.println("Initial: " + initial);
        System.out.println("InitialDiff: " + initialDiff);
        System.out.println("InitialPlusDiff: " + initialPlusDiff);
        System.out.println("Result: " + result);

        double d = 0.001;
        assertEquals(1.0 -0.00001*34.1421, result.x(), d);
        assertEquals(1.0 -0.00001*24.1421, result.y(), d);
        assertEquals(-10.0*2-10*2*1/Math.sqrt(2), result.vx(), d);
        assertEquals(-10.0-10*2*1/Math.sqrt(2), result.vy(), d);
    }
}
