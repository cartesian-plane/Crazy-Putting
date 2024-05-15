package org.ken22.odesolver.methods;

import org.ken22.interfaces.IFunc;
import org.ken22.interfaces.ODESolution;
import org.ken22.interfaces.ODESystem;

import java.util.ArrayList;

public class EulerMethod extends ODESolverMethod {

    /**
     * Initialize the Euler solver.
     * Inherited from {@link ODESolverMethod}
     *
     * @param system   the system of equations to solve
     * @param stepSize step size used by the solver
     * @param endTime the end point of the interval to solve, namely: [initialValue, endPoint]
     */
    public EulerMethod(ODESystem system, double stepSize, double startTime, double endTime) {
        super(system, stepSize, startTime, endTime);
    }

    @Override
    public ODESolution solve() {

        ArrayList<Double> time = new ArrayList<>();
        ArrayList<ArrayList<Double>> stateVectors = new ArrayList<>();
        ODESolution solution = new ODESolution(time, stateVectors);

        double t = startTime;
        ArrayList<Double> stateVector = system.getInitialStateVector();

        time.add(t);
        stateVectors.add(stateVector);

        long computationStartTime = System.nanoTime();

        while (t <= endTime) {
            t += stepSize;

            ArrayList<Double> updatedStateVector = new ArrayList<>();
            int i = 0;
            for (Double x : stateVector) {
                IFunc<Double, Double> function = this.system.getFunctions().get(i);
                double newX = x + stepSize * function.apply(stateVector);
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

}
