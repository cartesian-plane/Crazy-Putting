package controller;
import input.ODESystemFactory;
import interfaces.ODESolution;
import interfaces.ODESystem;
import interfaces.UserInput;
import odesolver.ODESolver;
import odesolver.methods.EulerMethod;
import odesolver.methods.ODESolverMethod;
import odesolver.methods.RungeKutta4;
import ui.InputPage;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ApplicationController {
    

    public static void main(String[] args) {
        ApplicationController app = new ApplicationController();
        app.run();
    }

    private void run() {
        InputPage InputPage = new InputPage(this);
    }

    public void onGenerate(UserInput input) {
        ODESystemFactory gen = new ODESystemFactory(input.initialValuesMap, input.equations);
        ODESystem syst = gen.getSyst();
        ODESolver solver = new ODESolver();

        System.out.println(syst);

        ODESolverMethod strategy;
        switch(input.methodType) {
            case EULER:
                strategy = new EulerMethod(syst, input.stepSize, input.startTime, input.endTime);
                break;
            case RUNGE_KUTTA_2:
                strategy = new RungeKutta4(syst, input.stepSize, input.startTime, input.endTime);
                break;
            case RUNGE_KUTTA_4:
                strategy = new RungeKutta4(syst, input.stepSize, input.startTime, input.endTime);
                break;
            default:
                //unreachable
                strategy = null;
        }
        solver.setStrategy(strategy);
        ODESolution solution = solver.solve();

        ArrayList<Double> timeValues = solution.getTime();
        ArrayList<ArrayList<Double>> stateVectors = solution.getStateVectors();

        try (PrintWriter writer = new PrintWriter("data/plot.csv")) {
            writer.println("Variable to plot: " + input.equationsType);
            for (int i = 0; i < timeValues.size(); i++) {
                Double time = timeValues.get(i);
                ArrayList<Double> stateVector = stateVectors.get(i);
                writer.println(time + "," + stateVector.toString());
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the CSV file: " + e.getMessage());
        }
    }
}