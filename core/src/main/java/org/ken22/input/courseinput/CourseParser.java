package org.ken22.input.courseinput;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class CourseParser {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File("assets/input/golf-course.json");
        try {

            JsonNode jsonNode = objectMapper.readTree(jsonFile);
            JsonNode golfCourseNode = jsonNode.get("golfCourse");
            String terrain = golfCourseNode.get("courseProfile").asText();
            System.out.println("Terrain: " + terrain);
        } catch (
            IOException e) {
            e.printStackTrace();
        }
    }

    private void doSomething() {

    }

}
