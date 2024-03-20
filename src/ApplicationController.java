import input.ODESolverGUI;
import input.ODESystemFactory;
import interfaces.ODESolution;
import interfaces.ODESystem;
import interfaces.UserInput;
import odesolver.ODESolver;
import odesolver.methods.EulerMethod;
import odesolver.methods.ODESolverMethod;
import odesolver.methods.RungeKutta4;

public class ApplicationController {
    

    public static void main(String[] args) {
        ApplicationController app = new ApplicationController();
        app.run();
    }

    private void run() {
        ODESolverGUI InputPage = new ODESolverGUI();
    }

    private void onGenerate(UserInput input) {
        ODESystemFactory gen = new ODESystemFactory(input.initialValuesMap, input.equations);
        ODESystem syst = gen.getSyst();
        ODESolver solver = new ODESolver();
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
    }
}