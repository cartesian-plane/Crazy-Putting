package org.ken22.input.courseinput;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CourseParser {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File("assets/input/golf-course.json");
        try {

            String terrain = "sin((x - y) / 7) + 0.5";
            System.out.println("Terrain: " + terrain);

            GolfCourse course = objectMapper.readValue(jsonFile, GolfCourse.class);

            System.out.println("Mass: " + course.mass());

            Expression expression = new ExpressionBuilder(terrain)
                .variables("x", "y")
                .build()
                .setVariable("x", 1)
                .setVariable("y", 2);
            double result = expression.evaluate();
            System.out.println("Terrain at (" + 1 + ", " + 2 + ")" + "= " + result);
            double trueResult = Math.sin( (1.0 - 2) / 7) + 0.5;
            System.out.println("True result: " + trueResult);
        } catch (
            IOException e) {
            e.printStackTrace();
        }
    }

}
