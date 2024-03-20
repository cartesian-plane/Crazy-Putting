package interfaces;

import odesolver.ODESolver;
import odesolver.methods.EulerMethod;
import odesolver.methods.ODESolverMethod;
import odesolver.methods.RungeKutta2;
import odesolver.methods.RungeKutta4;

import java.util.ArrayList;
import java.util.HashMap;

import input.ODESystemFactory;

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

        ArrayList<String> ex = new ArrayList<>(){
            {
                add("x' = x-x*y");
                add("y' = y-2*x*y");
            }
        };
        ArrayList<Number> init = new ArrayList<>();
        init.add(10); init.add(20);
        HashMap<String, Number> in = new HashMap<>();
        in.put("x", 10.0);
        in.put("y", 20.0);
        ODESystemFactory gen = new ODESystemFactory(in, ex);
        gen.getSyst();
        ODESolver solv = new ODESolver();
        ODESolverMethod stra = new RungeKutta4(gen.getSyst(), 0.000001, 0, 5);
        solv.setStrategy(stra);
        System.out.println(solv.solve());
    }
}
