package org.ken22.physics.odesolver.methods;

import org.ken22.interfaces.IFunc;
import org.ken22.interfaces.ODESolution;
import org.ken22.interfaces.ODESystem;
import java.util.ArrayList;

public class RungeKutta4 extends ODESolverMethod {

    /**
     * Initialize the Runge Kutta 4 solver.
     * Inherited from {@link ODESolverMethod
     * @param system   the system of equations to solve
     * @param stepSize step size used by the solver
     * @param endTime  the end point of the interval to solve, namely: [initialValue, endPoint]
     */
    public RungeKutta4(ODESystem system, double stepSize, double startTime, double endTime) {
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

            ArrayList<Double> k1 = new ArrayList<>();
            ArrayList<Double> k2 = new ArrayList<>();
            ArrayList<Double> k3 = new ArrayList<>();
            ArrayList<Double> k4 = new ArrayList<>();

            int j1 = 0;
            for (Double y : stateVector) {
                IFunc<Double, Double> function2 = this.system1.getFunctions().get(j1);
                double k1_increment = stepSize * function2.apply(stateVector);
                k1.add(k1_increment);
//                System.out.println("k1: " + k1_increment);
                j1++;
            }


            ArrayList<Double> k1_half = new ArrayList<>();
            int idx1 = 0;
            for (Double y : stateVector) {
                k1_half.add(y+k1.get(idx1)*0.5);
                idx1++;
            }

            int j2 = 0;
            for (Double y : stateVector) {
                IFunc<Double, Double> function2 = this.system1.getFunctions().get(j2);
                double newY = stepSize * function2.apply(k1_half);
                k2.add(newY);
                j2++;
            }

            ArrayList<Double> k2_half = new ArrayList<>();
            int idx2 = 0;
            for (Double y : stateVector) {
                k2_half.add(y+k2.get(idx2)*0.5);
                idx2++;
            }

            int j3 = 0;
            for (Double y : stateVector) {
                IFunc<Double, Double> function2 = this.system1.getFunctions().get(j3);
                double newY = stepSize * function2.apply(k2_half);
                k3.add(newY);
                j3++;
            }

            ArrayList<Double> k3_vector = new ArrayList<>();
            int idx3 = 0;
            for (Double y : stateVector) {
                k3_vector.add(y+k3.get(idx3)*0.5);
                idx3++;
            }

            int j4 = 0;
            for (Double y : stateVector) {
                IFunc<Double, Double> function2 = this.system1.getFunctions().get(j4);
                double newY = stepSize * function2.apply(k3_vector);
                k4.add(newY);
                j4++;
            }

            ArrayList<Double> updatedStateVector = new ArrayList<>();
            int i = 0;
            for (Double x : stateVector) {
                double newX = x + (k1.get(i) + 2*k2.get(i) + 2*k3.get(i) + k4.get(i))/6;
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
