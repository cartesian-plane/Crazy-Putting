package org.ken22.physics.odesolver.methods;

import org.ken22.interfaces.IFunc;
import org.ken22.interfaces.ODESolution;
import org.ken22.interfaces.ODESystem;

import java.util.ArrayList;

public class RungeKutta2 extends ODESolverMethod {

    /**
     * Initialize the Runge Kutta 2 solver.
     * Inherited from {@link ODESolverMethod}
     * @param system   the system of equations to solve
     * @param stepSize step size used by the solver
     * @param endTime the end point of the interval to solve, namely: [initialValue, endPoint]
     */
    public RungeKutta2(ODESystem system, double stepSize, double startTime, double endTime) {
        super(system, stepSize, startTime, endTime);
    }

    @Override
    public ODESolution solve() {
        ArrayList<Double> time = new ArrayList<>();
        ArrayList<ArrayList<Double>> stateVectors = new ArrayList<>();
        ODESolution solution = new ODESolution(time, stateVectors);
        double t = startTime;
        ArrayList<Double> stateVector = system1.getInitialStateVector();
        time.add(t);
        stateVectors.add(stateVector);

        long computationStartTime = System.nanoTime();

        while (t <= endTime) {
            t += stepSize;
            ArrayList<Double> updatedStateVector = new ArrayList<>();
            int i = 0;
            ArrayList<Double> intermediateStateVector = update(stateVector, stepSize / 2);
            for (Double x : stateVector) {
                IFunc<Double, Double> function = this.system1.getFunctions().get(i);
                double newX = x.doubleValue() + stepSize * function.apply(intermediateStateVector).doubleValue();
                updatedStateVector.add(newX);
                i++;
            }
            stateVectors.add(updatedStateVector);
            time.add(t);
            stateVector = updatedStateVector;
        }

        long elapsedComputationTime = System.nanoTime() - computationStartTime;
        solution.setTimeTaken(elapsedComputationTime);

        return solution;
    }

    private ArrayList<Double> update(ArrayList<Double> stateVector, double stepSize) {

        int i = 0;
        ArrayList<Double> intermediateStateVector = new ArrayList<>();
        for (Double x : stateVector) {
            IFunc<Double, Double> function2 = this.system1.getFunctions().get(i);
            double newY = x.doubleValue() + stepSize * function2.apply(stateVector).doubleValue();
            intermediateStateVector.add(newY);
        }

        return intermediateStateVector;
    }
}
