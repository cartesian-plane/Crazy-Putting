package org.ken22.physicsx.differentiation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ken22.input.courseinput.GolfCourse;

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

        assertEquals(true, true);
    }

}

