package interfaces;

import odesolver.ODESolver;
import odesolver.methods.EulerMethod;
import odesolver.methods.ODESolverMethod;

import java.util.ArrayList;

public class ODESystemTestFactory {
    ODESystem system(ArrayList<Number> initialStateVector, ArrayList<IFunc<Number, Number>> functions) {
        return new ODESystem(initialStateVector, functions);
    }

    ODESystem testSyst() {
        ArrayList<Number> initialStateVector = new ArrayList<>();
        ArrayList<IFunc<Number, Number>> functions = new ArrayList<>();

        ArrayList<Number> vars = new ArrayList<>();
        vars.add(1.0);
        vars.add(2.0);
        vars.add(3.0);

        initialStateVector.add(vars.get(0));
        initialStateVector.add(vars.get(1));
        initialStateVector.add(vars.get(2));

        functions.add( (varss) -> {return varss.get(1); });
        functions.add( (varss) -> {return varss.get(2); });
        functions.add( (varss) -> {return - varss.get(0).doubleValue() -2*varss.get(1).doubleValue() - varss.get(2).doubleValue(); });

        return new ODESystem(initialStateVector, functions);
    }

    public static void main(String[] args) {
        ODESystemTestFactory factory = new ODESystemTestFactory();
        ODESystem syst = factory.testSyst();
        System.out.println(syst + "\n" + syst.derivative());

        ODESolver solverContext = new ODESolver();
        ODESolverMethod eulerSolver = new EulerMethod(syst, 0.00001, 0, 100);
        solverContext.setStrategy(eulerSolver);
        ODESolution solution = solverContext.solve();
        System.out.println("Initial state vector");
        System.out.println(solution.stateVectors.get(2));


    }
}
