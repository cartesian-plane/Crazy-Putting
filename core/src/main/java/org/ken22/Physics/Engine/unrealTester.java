package org.ken22.Physics.Engine;

import org.ken22.Physics.Numerical_Derivation.NumDerivationMethod;
import org.ken22.Physics.Numerical_Derivation.basicDerivation;
import org.ken22.Physics.Numerical_Integration.NumIntegrationMethod;
import org.ken22.Physics.Numerical_Integration.RK4;
import org.ken22.Physics.System.PhysicsSystem;
import org.ken22.Physics.Vectors.GVec4;
import org.ken22.input.courseinput.CourseParser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.interfaces.IFunc;

public class unrealTester {

    public static void main(String[] args) {

        RK4 integrator = new RK4();
        basicDerivation differenatiator = new basicDerivation();
        GVec4 initialState = new GVec4(0.0, 0.0, 0.0, 1.0, 1.0, 0.01);
        File coursejson = new File("assets/input/golf-course.json");
        testRun("unrealTest1", coursejson, initialState, differenatiator, integrator);
    }

    public static void testRun(String name, File coursejson, GVec4 initialState, NumDerivationMethod differentiator, NumIntegrationMethod integrator) {

        PhysicsSystem system = new PhysicsSystem(initialState,coursejson);
        unrealEngine engine = new unrealEngine(system, integrator, differentiator);
        engine.run();

        ArrayList<GVec4> vectors = engine.getStateVectors();;
        String filePath = "assets/" + name + ".csv";

        try (
            FileWriter csvWriter = new FileWriter(filePath)) {
            // Write header
            csvWriter.append("time");
            csvWriter.append(",");
            csvWriter.append("x");
            csvWriter.append(",");
            csvWriter.append("y");
            csvWriter.append(",");
            csvWriter.append("vx");
            csvWriter.append(",");
            csvWriter.append("vy");
            csvWriter.append(",");
//            csvWriter.append("dhdx");
//            csvWriter.append(",");
//            csvWriter.append("dhdy");
//            csvWriter.append(",");
            csvWriter.append("height");
            csvWriter.append("\n");

            // Write data
            for (GVec4 vector : vectors) {
                for (Double value : vector.getVector()) {
                    csvWriter.append(String.valueOf(value));
                    csvWriter.append(",");
                }
                csvWriter.append("\n");
            }

            csvWriter.flush();
            csvWriter.close();

            System.out.println("CSV file written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
