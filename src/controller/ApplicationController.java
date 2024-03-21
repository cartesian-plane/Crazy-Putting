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
import ui.PhaseSpace;
import ui.Table;

public class ApplicationController {
       
    private PhaseSpace phaseSpace;
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
        ODESolution solution = solver.solve();
        System.out.println(solution);

        if(input.graph) {
            //
        }
        if (input.table) {
            new Table(syst.getVariables(), solution);
        }
        //System.out.println(input.phase);
        if (input.phase) {
            phaseSpace = new PhaseSpace(syst, solution);
            phaseSpace.setVisible(true);
        }
    }
}