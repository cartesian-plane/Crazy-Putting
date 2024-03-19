package odesolver.methods;

import interfaces.IFunc;
import interfaces.ODESolution;
import interfaces.ODESystem;

import java.util.ArrayList;

public class RungeKutta2 extends ODESolverMethod {

    /**
     * Initialize the Runge Kutta 2 solver.
     * Inherited from {@link ODESolverMethod}
     *
     * @param system   the system of equations to solve
     * @param stepSize step size used by the solver
     * @param endTime the end point of the interval to solve, namely: [initialValue, endPoint]
     */
    public RungeKutta2(ODESystem system, double stepSize, double startTime, double endTime) {
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

        while (t <= endTime) {
            t += stepSize;

            ArrayList<Number> updatedStateVector = new ArrayList<>();

            int i = 0;
            for (Number x : stateVector) {


                // build the intermediate state vector
                ArrayList<Number> intermediateStateVector = new ArrayList<>();
                int j = 0;
                for (Number y : stateVector) {
                    IFunc<Number, Number> function2 = this.system.getFunctions().get(j);
                    double halfStepSize = stepSize / 2;
                    double newY = (double)y + halfStepSize * function2.apply(stateVector).doubleValue();
                    intermediateStateVector.add(newY);
                }

                IFunc<Number, Number> function = this.system.getFunctions().get(i);
                double newX = (double)x + stepSize * function.apply(updatedStateVector).doubleValue();

                updatedStateVector.add(newX);
                i++;

            }

            stateVectors.add(updatedStateVector);
            time.add(t);
            stateVector = updatedStateVector;
        }

        return solution;
    }
}
