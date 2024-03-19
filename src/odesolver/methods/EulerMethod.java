package odesolver.methods;

import interfaces.IFunc;
import interfaces.ODESolution;
import interfaces.ODESystem;

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

        ArrayList<Number> time = new ArrayList<>();
        ArrayList<ArrayList<Number>> stateVectors = new ArrayList<>();
        ODESolution solution = new ODESolution(time, stateVectors);

        double t = startTime;
        ArrayList<Number> stateVector = system.getInitialStateVector();

        time.add(t);
        stateVectors.add(stateVector);

        long computationStartTime = System.nanoTime();

        while (t <= endTime) {
            t += stepSize;

            ArrayList<Number> updatedStateVector = new ArrayList<>();
            int i = 0;
            for (Number x : stateVector) {
                IFunc<Number, Number> function = this.system.getFunctions().get(i);
                double newX = (double) x + stepSize * function.apply(stateVector).doubleValue();
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
