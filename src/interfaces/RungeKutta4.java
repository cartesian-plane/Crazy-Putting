package interfaces;

import odesolver.methods.ODESolverMethod;

import java.util.ArrayList;

public class RungeKutta4 extends ODESolverMethod {

    /**
     * Initialize the Runge Kutta 4 solver.
     * Inherited from {@link ODESolverMethod}
     *
     * @param system   the system of equations to solve
     * @param stepSize step size used by the solver
     * @param endTime  the end point of the interval to solve, namely: [initialValue, endPoint]
     */
    public RungeKutta4(ODESystem system, double stepSize, double startTime, double endTime) {
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

//            ArrayList<Number> k1 = new ArrayList<>();
            // k1 is the same as the state vector
            ArrayList<Number> k2 = update(stateVector, stepSize / 2);
            ArrayList<Number> k3 = update(k2, stepSize / 2);
            ArrayList<Number> k4 = update(k3, stepSize);


            ArrayList<Number> updatedStateVector = new ArrayList<>();
            int i = 0;
            for (Number x : stateVector) {
                // build the intermediate state vector

                IFunc<Number, Number> function = this.system.getFunctions().get(i);
                double newX = (double) x + stepSize / 6 * (function.apply(stateVector).doubleValue() +
                        2 * function.apply(k2).doubleValue() +
                        2 * function.apply(k3).doubleValue() +
                        function.apply(k4).doubleValue());

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

    private ArrayList<Number> update(ArrayList<Number> stateVector, double stepSize) {
        int i = 0;

        ArrayList<Number> intermediateStateVector = new ArrayList<>();
        for (Number x : stateVector) {
            IFunc<Number, Number> function2 = this.system.getFunctions().get(i);
            double newY = (double) x + stepSize * function2.apply(stateVector).doubleValue();
            intermediateStateVector.add(newY);
        }

        return intermediateStateVector;
    }

}
