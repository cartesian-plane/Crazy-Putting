package odesolver.testing;

import interfaces.ODESolution;
import interfaces.ODESystem;
import interfaces.ODESystemTestFactory;
import odesolver.ODESolver;
import odesolver.methods.EulerMethod;
import odesolver.methods.RungeKutta2;
import odesolver.methods.RungeKutta4;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

public class Tester {
    /**
     * Equation used for computing the analytical solution.
     * Written by hand.
     */
    private static Function<Double, Double> func = t -> Math.exp(2*t);

    /**
     * System used for computing the numerical solution.
     */
    private static ODESystem numericalSystem;

    public static void main(String[] args) throws IOException {
        numericalSystem = new ODESystemTestFactory().testSyst();

        FileWriter csvWriter = new FileWriter("errors.csv");
        csvWriter.append("Step Size");
        csvWriter.append(",");
        csvWriter.append("Global Error");
        csvWriter.append("\n");

        double t = 0;
        double endTime = 300;

        for (double stepSize = 0.0001; stepSize <= 0.01; stepSize += 0.0001) {
            // keep changing this to test stuff
            ODESolver solver = new ODESolver(new RungeKutta4(numericalSystem, stepSize, 0, endTime));
            ODESolution numericalSolution = solver.solve();

            ArrayList<Number> tValues = new ArrayList<>();
            tValues.add(t);
            ArrayList<Number> yValues = new ArrayList<>();
            yValues.add(func.apply(t));

            double y;
            while (t <= endTime) {
                t += stepSize;
                y = func.apply(t);
                tValues.add(t);
                yValues.add(y);
            }

            double globalError = getGlobalError(numericalSolution, tValues, yValues);
            System.out.println("Step size: " + stepSize + ", Global error: " + globalError);

            csvWriter.append(String.valueOf(stepSize));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(globalError));
            csvWriter.append("\n");

            t = 0;
        }


    }

    public static double getGlobalError(ODESolution solution, ArrayList<Number> tValues, ArrayList<Number> yValues) {
        double sum = 0;

        ArrayList<ArrayList<Number>> vectors = solution.getStateVectors();

        int i = 0;
        for (ArrayList<Number> vector : vectors) {
            double numerical_y = vector.getFirst().doubleValue();
            double true_y = yValues.get(i).doubleValue();

            sum += Math.abs(numerical_y - true_y);

            i++;
        }

        return sum;
    }
}
