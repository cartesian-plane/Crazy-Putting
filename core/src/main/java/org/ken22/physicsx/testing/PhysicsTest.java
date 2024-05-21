package org.ken22.physicsx.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physicsx.engine.PhysicsEngine;
import org.ken22.physicsx.vectors.StateVector4;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PhysicsTest {
    public static void main(String[] args) {
        File courseFile = new File("assets/input/golf-course.json");
        ObjectMapper objectMapper = new ObjectMapper();
        GolfCourse course = null;

        try {
            course = objectMapper.readValue(courseFile, GolfCourse.class);

        } catch (
            IOException e) {
            e.printStackTrace();
        }

        System.out.println(course.courseProfile());

        StateVector4 initialState = new StateVector4(0, 0, 1, 1);
        PhysicsEngine engine = new PhysicsEngine(course, initialState);

        StateVector4 previous  = initialState;
        StateVector4 current = null;

        ArrayList<StateVector4> trajectory = new ArrayList<>();

        while (!engine.isAtRest()) {
            previous = current;
//            System.out.println("previous = " + previous);
//            System.out.println("current = " + current);

            current = engine.iterator().next();
            trajectory.add(current);
        }

        String filePath = "assets/enginetest.csv";
        System.out.println("Starting write");
        writeToCSV(trajectory, filePath);
    }

    private static void writeToCSV(ArrayList<StateVector4> trajectory, String filePath) {
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {

            writer.println("x,y,vx,vy");

            for (StateVector4 vector : trajectory) {
                writer.println(vector.x() + "," + vector.y() + "," + vector.vx() + "," + vector.vy());
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing the trajectory to a CSV file.");
            e.printStackTrace();
        }
    }
}
