package org.ken22.physicsx.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physicsx.vectors.StateVector4;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class PhysicsEngineTest {

    @Test
    void isAtRest() {
        GolfCourse course = course();
        // Expression expr = expr(course);
        StateVector4 initialStateVector = new StateVector4(0.0, 0.0, 1.0, 1.0);
        PhysicsEngine engine = new PhysicsEngine(course, initialStateVector);
        Iterator<StateVector4> iterator = engine.new frameRateIterator();
        while (iterator.hasNext()) {
            StateVector4 stateVector4 = iterator.next();
            System.out.println(stateVector4.toString());
        }
    }

    GolfCourse course() {
        GolfCourse course;

        File resourcesDirectory = new File("src/test/resources");
        File courseFile = new File(resourcesDirectory, "true-golf-course.json");

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            course = objectMapper.readValue(courseFile, GolfCourse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return course;
    }
}
