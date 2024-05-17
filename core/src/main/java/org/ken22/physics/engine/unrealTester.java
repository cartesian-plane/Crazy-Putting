package org.ken22.physics.engine;

import org.ken22.physics.numerical_derivation.NumDerivationMethod;
import org.ken22.physics.numerical_derivation.basicDerivation;
import org.ken22.physics.numerical_integration.NumIntegrationMethod;
import org.ken22.physics.numerical_integration.RK4;
import org.ken22.physics.system.PhysicsSystem;
import org.ken22.physics.vectors.GVec4;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class unrealTester {

    public static void main(String[] args) {

        RK4 integrator = new RK4();
        basicDerivation differentiator = new basicDerivation();
        GVec4 initialState = new GVec4(0.0, 0.0, 0.0, 1.0, 1.0, 0.01);
        File coursejson = new File("project-1-2/assets/input/golf-course.json");
        testRun("unrealTest1", coursejson, initialState, differentiator, integrator);
    }

    public static void testRun(String name, File coursejson, GVec4 initialState, NumDerivationMethod differentiator, NumIntegrationMethod integrator) {

        PhysicsSystem system = new PhysicsSystem(initialState,coursejson);
        unrealEngine engine = new unrealEngine(system, integrator, differentiator);

        double t0 = System.nanoTime();
        boolean atRest = engine.isAtRest(engine.getInitialState());
        while(!atRest) {
            GVec4 newState = engine.nextStep();
            atRest = engine.isAtRest(newState);
            double currentTime = System.nanoTime();
            double elapsedTime = (currentTime - t0) / 1e9;
            if(elapsedTime >= 100.0) {
                atRest = true;
            }
        }

        ArrayList<GVec4> vectors = engine.getStateVectors();;
        String filePath = "project-1-2/assets/" + name + ".csv";

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
            csvWriter.append("dhdx");
            csvWriter.append(",");
            csvWriter.append("dhdy");
            csvWriter.append(",");
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
