package org.ken22.physicsx.differentiation;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physicsx.vectors.StateVector4;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class VectorDifferentiationFactoryTest {

    @Test
    @DisplayName("Dummy test")
    void vectorDifferentiation4() {
        GolfCourse course;

        File resourcesDirectory = new File("src/test/resources");
        File courseFile = new File(resourcesDirectory, "golf-course.json");

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            course = objectMapper.readValue(courseFile, GolfCourse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Expression expr = new ExpressionBuilder(course.courseProfile())
            .variables("x", "y")
            .build();

        VectorDifferentiationFactory vectorDifferentiationFactory = new VectorDifferentiationFactory(0.00001, expr, course, new FivePointCenteredDifference());
        VectorDifferentiation4 vd = vectorDifferentiationFactory.vectorDifferentiation4(0.0, 0.0);

        //assertEquals();

        StateVector4 sv = new StateVector4(0.0, 0.0, 1.0, 1.0);
        //System.out.println(vd.dsv(sv).toString());
        assertEquals(vd.dsv(sv), new StateVector4(1.0, 1.0, -10.0*2-10*2*1/Math.sqrt(2), -10.0-10*2*1/Math.sqrt(2)));
    }

}

