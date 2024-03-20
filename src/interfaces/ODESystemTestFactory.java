package interfaces;

import odesolver.ODESolver;
import odesolver.methods.EulerMethod;
import odesolver.methods.ODESolverMethod;

import java.util.ArrayList;
import java.util.HashMap;

import input.ODESystGenerator;

public class ODESystemTestFactory {
    ODESystem system(ArrayList<Number> initialStateVector, ArrayList<IFunc<Number, Number>> functions) {
        return new ODESystem(initialStateVector, functions);
    }

    ODESystem testSyst() {
        ArrayList<Number> initialStateVector = new ArrayList<>();
        ArrayList<IFunc<Number, Number>> functions = new ArrayList<>();

        ArrayList<Number> vars = new ArrayList<>();
        vars.add(1.0);

        initialStateVector.add(vars.get(0));

        functions.add( (varss) -> {return 2*varss.get(0).doubleValue(); });

        return new ODESystem(initialStateVector, functions);
    }

    public static void main(String[] args) {
        // ODESystemTestFactory factory = new ODESystemTestFactory();
        // ODESystem syst = factory.testSyst();
        // System.out.println(syst + "\n" + syst.derivative());

        // ODESolver solverContext = new ODESolver();
        // ODESolverMethod strat = new RungeKutta2(syst, 0.00001, 0, 100);
        // solverContext.setStrategy(strat);
        // ODESolution solution = solverContext.solve();
        // System.out.println("Initial state vector");
        // System.out.println(solution.stateVectors.get(100000));
        // ex 2

        String ex1 = "x' = 1";
        String ex2 = "y' = 2";
        ArrayList<Number> init = new ArrayList<>();
        init.add(10); init.add(20);
        HashMap<String, Number> in = new HashMap<>();
        in.put("x", 3.0);
        in.put("y", 1.0);
        ODESystGenerator gen = new ODESystGenerator(in, ex1, ex2);
        gen.getSyst();
        ODESolver solv = new ODESolver();
        ODESolverMethod stra = new EulerMethod(gen.getSyst(), 0.01, 0, 10);
        solv.setStrategy(stra);
        System.out.println(solv.solve());
    }
}
