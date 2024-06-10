package org.ken22.physics.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.jupiter.api.Test;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.differentiators.FivePointCenteredDifference;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PhysicsUtilsTest {

    @Test
    void magnitude() {
    }

    @Test
    void xSlope() {
        GolfCourse course = course();
        Expression expr = expr(course);

        assertEquals(2.0, PhysicsUtils.xSlope(0.0, 0.0, 0.001, expr, new FivePointCenteredDifference()), 0.0000000001);
    }

    @Test
    void ySlope() {
        GolfCourse course = course();
        Expression expr = expr(course);

        assertEquals(1.0, PhysicsUtils.ySlope(0.0, 0.0, 0.001, expr, new FivePointCenteredDifference()), 0.0000000001);
    }

    GolfCourse course() {
        GolfCourse course;

        File resourcesDirectory = new File("src/test/resources");
        File courseFile = new File(resourcesDirectory, "golf-course.json");

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            course = objectMapper.readValue(courseFile, GolfCourse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return course;
    }
    Expression expr(GolfCourse course) {
        return new ExpressionBuilder(course.courseProfile())
            .variables("x", "y")
            .build();
    }
}
