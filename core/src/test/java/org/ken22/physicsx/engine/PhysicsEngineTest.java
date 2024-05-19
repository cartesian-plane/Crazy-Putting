package org.ken22.physicsx.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physicsx.vectors.StateVector4;

import java.io.File;
import java.io.IOException;

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
}
