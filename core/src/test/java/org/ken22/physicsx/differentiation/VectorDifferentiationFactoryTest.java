package org.ken22.physicsx.differentiation;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physicsx.TestUtils;
import org.ken22.physicsx.differentiators.FivePointCenteredDifference;
import org.ken22.physicsx.vectors.StateVector4;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class VectorDifferentiationFactoryTest {

    @Test
    @DisplayName("Dummy test")
    @Disabled
    void normalSpeedVectorDifferentiation4() {
        GolfCourse course = TestUtils.course("golf-course.json");
        Expression expr = TestUtils.expr(course);

        InstantaneousVectorDifferentiationFactory instDiffFact =
            new InstantaneousVectorDifferentiationFactory(1, expr, course, new FivePointCenteredDifference());
        InstantaneousVectorDifferentiation4 instDiff = instDiffFact.instantaneousVectorDifferentiation4();

        VectorDifferentiationFactory factory =
            new VectorDifferentiationFactory(1, expr, course, new FivePointCenteredDifference());
        VectorDifferentiation4 vd = factory.normalSpeedVectorDifferentiation4();

        StateVector4 initial = new StateVector4(4.0, 4.0, 1.0, 1.0);
        StateVector4 result = vd.apply(1.0, initial);

        StateVector4 initialDiff = instDiff.apply(initial);
        StateVector4 initialPlusDiff = initial.add(initialDiff.multiply(1));
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

    @Test
    @DisplayName("Dummy test")
    void lowSpeedVectorDifferentiation4() {
        GolfCourse course = TestUtils.course("golf-course.json");
        Expression expr = TestUtils.expr(course);

        InstantaneousVectorDifferentiationFactory instDiffFact =
            new InstantaneousVectorDifferentiationFactory(0.00001, expr, course, new FivePointCenteredDifference());
        InstantaneousVectorDifferentiation4 instDiff = instDiffFact.altInstantaneousVectorDifferentiation4();

        VectorDifferentiationFactory factory =
            new VectorDifferentiationFactory(0.00001, expr, course, new FivePointCenteredDifference());
        VectorDifferentiation4 vd = factory.lowSpeedVectorDifferentiation4();

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
        assertEquals(-10.0*2-10*2*2/Math.sqrt(5), result.vx(), d);
        assertEquals(-10.0-10*2*1/Math.sqrt(5), result.vy(), d);
    }

    @Test
    void testNormalSpeedVectorDifferentiation4() {
    }

    @Test
    void testLowSpeedVectorDifferentiation4() {
    }
}

