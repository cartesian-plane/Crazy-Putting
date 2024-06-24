package org.ken22;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.differentiators.FivePointCenteredDifference;
import org.ken22.physics.engine.PhysicsEngine;
import org.ken22.physics.odesolvers.outofplace.RK4;
import org.ken22.physics.vectors.StateVector4;

import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;

public class TestUtils {
    public static GolfCourse course(String fileName) {
        GolfCourse course;

        File resourcesDirectory = new File("src/test/resources");
        File courseFile = new File(resourcesDirectory, fileName);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            course = objectMapper.readValue(courseFile, GolfCourse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return course;
    }
    public static Expression expr(GolfCourse course) {
        return new ExpressionBuilder(course.courseProfile())
            .variables("x", "y")
            .build();
    }

    public static BiFunction<StateVector4,GolfCourse, StateVector4> prediction = (state, course) -> {
        PhysicsEngine engine = new PhysicsEngine(course, state, 0.0001, new FivePointCenteredDifference(), new RK4());
        return engine.solve();
    };
}
