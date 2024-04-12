package org.ken22.controller;

import org.ken22.input.ODESystemFactory;
import org.ken22.interfaces.ODESystem;
import org.ken22.interfaces.UserInput;
import org.ken22.odesolver.ODESolver;
import org.ken22.odesolver.methods.EulerMethod;
import org.ken22.odesolver.methods.ODESolverMethod;
import org.ken22.odesolver.methods.RungeKutta4;
import org.ken22.ui.InputPage;
import org.ken22.ui.PhaseSpace;
import org.ken22.ui.Table;

public class ApplicationController {

    private PhaseSpace phaseSpace;
    private Table table;
    //private final GraphPanel graphPanel;

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
        org.ken22.interfaces.ODESolution solution = solver.solve();
        System.out.println(solution);

        if(input.graph) {
            //
        }
        if (input.table) {
            table = new Table(syst.getVariables(), solution);
            table.setVisible(true);
        }
        //System.out.println(input.phase);
        if (input.phase) {
            phaseSpace = new PhaseSpace(syst, solution);
            phaseSpace.setVisible(true);
        }
    }
}
