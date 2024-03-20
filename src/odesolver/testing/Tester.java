package odesolver.testing;

import interfaces.ODESolution;
import interfaces.ODESystem;
import odesolver.ODESolver;
import odesolver.methods.EulerMethod;

import java.util.ArrayList;
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

    public static void main(String[] args) {
        numericalSystem = ;
        ODESolver solver = new ODESolver(new EulerMethod(numericalSystem, 0.001, 0, 1));
        ODESolution numericalSolution = solver.solve();

        double t = 0;
        double endTime = 1;
        double stepSize = 0.001;
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
