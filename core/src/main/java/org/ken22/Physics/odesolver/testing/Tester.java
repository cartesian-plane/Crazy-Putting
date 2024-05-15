package org.ken22.Physics.odesolver.testing;

import org.ken22.Physics.odesolver.methods.EulerMethod;
import org.ken22.Physics.odesolver.methods.RungeKutta4;
import org.ken22.interfaces.IFunc;
import org.ken22.interfaces.ODESolution;
import org.ken22.interfaces.ODESystem;
import org.ken22.Physics.odesolver.ODESolver;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class Tester {
    /**
     * Equation used for computing the analytical solution.
     * Written by hand.
     */
    private static Function<Double, Double> func = t -> Math.exp(t);

    /**
     * System used for computing the numerical solution.
     */
    private static ODESystem numericalSystem;

    public static void main(String[] args) throws IOException {
        testEuler();
        testRK4();
    }

    public static double getGlobalError(ODESolution solution, double endTime) {
        double sum = 0;

        ArrayList<ArrayList<Double>> vectors = solution.getStateVectors();
        double numerical_y = vectors.get(vectors.size() - 1).get(0);
        double true_y = func.apply(endTime);
        sum += Math.abs(numerical_y - true_y);

        // Normalize the error
        return sum/vectors.size();
    }

    public static void testEuler() throws IOException {

        ArrayList<Double> initialStateVector = new ArrayList<>();
        ArrayList<IFunc<Double, Double>> functions = new ArrayList<>();
        ArrayList<Double> vars = new ArrayList<>();
        vars.add(1.0);
        initialStateVector.add(vars.get(0));
        functions.add(
            (varss) -> { return varss.get(0); }
        );
        HashMap<String, Integer> varOrder = new HashMap<>() {
            {
                put("X", 0);
            }
        };

        ODESystem eulerSystem = new ODESystem(varOrder, initialStateVector, functions);

        FileWriter eulerCsvWriter = new FileWriter("/Users/leo/Desktop/Output/errorseuler.csv");
        eulerCsvWriter.append("Step Size");
        eulerCsvWriter.append(",");
        eulerCsvWriter.append("Global Error");
        eulerCsvWriter.append("\n");

        double t = 0;
        double endTime = 50;

        for (double stepSize = 0.0001; stepSize <= 1; stepSize += 0.0001) {
            // keep changing this to test stuff
            ODESolver solver = new ODESolver(new EulerMethod(eulerSystem, stepSize, 0, endTime));
            ODESolution numericalSolution = solver.solve();

            int iterations = (int) (endTime/stepSize);
            double globalError = getGlobalError(numericalSolution, endTime);
            System.out.println("Step size: " + stepSize + ", Global error: " + globalError);

            eulerCsvWriter.append(String.valueOf(stepSize));
            eulerCsvWriter.append(",");
            eulerCsvWriter.append(String.valueOf(globalError));
            eulerCsvWriter.append("\n");

            t = 0;
        }
    }

    public static void testRK4() throws IOException {

        ArrayList<Double> initialStateVector = new ArrayList<>();
        ArrayList<IFunc<Double, Double>> functions = new ArrayList<>();
        ArrayList<Double> vars = new ArrayList<>();
        vars.add(1.0);
        initialStateVector.add(vars.get(0));
        functions.add(
            (varss) -> { return varss.get(0); }
        );
        HashMap<String, Integer> varOrder = new HashMap<>() {
            {
                put("X", 0);
            }
        };

        ODESystem rk4System = new ODESystem(varOrder, initialStateVector, functions);

        FileWriter rk4CsvWriter = new FileWriter("/Users/leo/Desktop/Output/errorsrk4.csv");
        rk4CsvWriter.append("Step Size");
        rk4CsvWriter.append(",");
        rk4CsvWriter.append("Global Error");
        rk4CsvWriter.append("\n");

        double t = 0;
        double endTime = 50;

        for (double stepSize = 0.0001; stepSize <= 1; stepSize += 0.0001) {
            // keep changing this to test stuff
            ODESolver solver = new ODESolver(new RungeKutta4(rk4System, stepSize, 0, endTime));
            ODESolution numericalSolution = solver.solve();

            double globalError = getGlobalError(numericalSolution, endTime);
            System.out.println("Step size: " + stepSize + ", Global error: " + globalError);

            rk4CsvWriter.append(String.valueOf(stepSize));
            rk4CsvWriter.append(",");
            rk4CsvWriter.append(String.valueOf(globalError));
            rk4CsvWriter.append("\n");

            t = 0;
        }
    }
}
