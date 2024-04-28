package org.ken22.Physics.Engine;

import org.ken22.interfaces.IFunc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.Math;

public class PhysicsTester {
    public static void main(String[] args) throws IOException {

        //double x, double y, double vx, double vy, double timeStep, double startTime, double endTime, double kFrictionCoef, double sFrictionCoef, double gCoef, IFunc<Double, Double> height
        IFunc<Double, Double> testFunc1 = (vars) -> (Math.sin(vars.get(0))*Math.sin(vars.get(1)));
        PhysicsEngine test1 = new PhysicsEngine((Math.PI)/2,(Math.PI)/2,0.5,0.5, 0.001, 0, 1, 0.1, 0.2, 9.80665, testFunc1, "sin");
        testSystem(test1);

        IFunc<Double, Double> testFunc2 = (vars) -> ((vars.get(0)+vars.get(1))/2);
        PhysicsEngine test2 = new PhysicsEngine(4,4,1,1, 0.001, 0, 3, 0.1, 0.2, 9.80665, testFunc2, "plane");
        System.out.println(test2.getStateVectors());
        testSystem(test2);

        IFunc<Double, Double> testFunc3 = (vars) -> (1.0);
        PhysicsEngine test3 = new PhysicsEngine(4,4,1,1, 0.001, 0, 3, 0.1, 0.2, 9.80665, testFunc3, "flat_plane");
        System.out.println(test3.getStateVectors());
        testSystem(test3);
    }

    public static void testSystem(PhysicsEngine engine) throws IOException {

        engine.solve();
        ArrayList<ArrayList<Double>> vectors = engine.getStateVectors();;

        String filePath = "/Users/leo/Desktop/Y1/Semester 2/Project 2/Matlab/" + engine.getName() + ".csv";

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
            for (ArrayList<Double> vector : vectors) {
                for (Double value : vector) {
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



