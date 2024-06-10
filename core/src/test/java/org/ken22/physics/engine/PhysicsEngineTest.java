package org.ken22.physics.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.vectors.StateVector4;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class PhysicsEngineTest {

    @Test
    @DisplayName("Underwater collision")
    void underwater() {
        GolfCourse course;

        File resourcesDirectory = new File("src/test/resources");
        File courseFile = new File(resourcesDirectory, "golf-course.json");

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            course = objectMapper.readValue(courseFile, GolfCourse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Start the ball at a negative height
        StateVector4 initialState = new StateVector4(-2, 0, 0, 0);
        PhysicsEngine engine = new PhysicsEngine(course, initialState);

        assertTrue(engine.underwater());
    }

    @Test
    /**
     * Not actually testing isAtRest(). Just running the iterator.
     */
    void isAtRest() {
        GolfCourse course = course();
        // Expression expr = expr(course);
        StateVector4 initialStateVector = new StateVector4(0.0, 0.0, 1.0, 1.0);
        PhysicsEngine engine = new PhysicsEngine(course, initialStateVector);
        Iterator<StateVector4> iterator = engine.new FrameRateIterator();
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
