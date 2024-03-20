package interfaces;

import odesolver.ODESolver;
import odesolver.methods.EulerMethod;
import odesolver.methods.ODESolverMethod;
import odesolver.methods.RungeKutta2;

import java.util.ArrayList;
import java.util.HashMap;

import input.ODESystGenerator;

public class ODESystemTestFactory {
    ODESystem system(ArrayList<Number> initialStateVector, ArrayList<IFunc<Number, Number>> functions) {
        return new ODESystem(initialStateVector, functions);
    }

    private ODESystem LotkaVolterra(double initialX, double initialY, double alpha, double beta,
                                    double delta, double gamma) {
        ArrayList<Number> initialStateVector = new ArrayList<>();
        ArrayList<IFunc<Number, Number>> functions = new ArrayList<>();

        // initial conditions
        ArrayList<Number> vars = new ArrayList<>();
        vars.add(initialX);
        vars.add(initialY);

        initialStateVector.add(vars.get(0));
        initialStateVector.add(vars.get(1));

        functions.add( (varss) -> {return alpha*varss.get(0).doubleValue() - beta*varss.get(0).doubleValue()
                *varss.get(1).doubleValue(); });
        functions.add( (varss) -> {return delta*varss.get(0).doubleValue()*varss.get(1).doubleValue() -
                gamma*varss.get(1).doubleValue(); });

        return new ODESystem(initialStateVector, functions);
    }

    private ODESystem FitzHughNagumo() {
        ArrayList<Number> initialStateVector = new ArrayList<>();
        ArrayList<IFunc<Number, Number>> functions = new ArrayList<>();
        ArrayList<Number> vars = new ArrayList<>();
        vars.add(1.0);
        vars.add(0.0);
        initialStateVector.add(vars.get(0));
        functions.add( (systemVars) -> {return systemVars.getFirst().doubleValue() - Math.pow(systemVars.getFirst().doubleValue(),3)/3 - systemVars.get(1).doubleValue() + 0.1;});
        functions.add( (systemVars) -> {return 0.05*(systemVars.getFirst().doubleValue() + 0.95 - 0.91*systemVars.get(1).doubleValue() + 0.1);});
        return new ODESystem(initialStateVector, functions);
    }

    private ODESystem SIR(double initialS, double initialI, double initialR, double k, double mu, double gamma) {
        ArrayList<Number> initialStateVector = new ArrayList<>();
        ArrayList<IFunc<Number, Number>> functions = new ArrayList<>();

        // initial conditions
        ArrayList<Number> vars = new ArrayList<>();
        vars.add(initialS);
        vars.add(initialI);
        vars.add(initialR);

        initialStateVector.add(vars.get(0));
        initialStateVector.add(vars.get(1));
        initialStateVector.add(vars.get(2));

        functions.add( (varss) -> {return -k*varss.get(0).doubleValue()*varss.get(1).doubleValue() +
                mu*(1 - varss.get(0).doubleValue()); });
        functions.add( (varss) -> {return k*varss.get(0).doubleValue()*varss.get(1).doubleValue() -
                (gamma + mu) * varss.get(1).doubleValue(); });
        functions.add( (varss) -> {return gamma*varss.get(1).doubleValue() - mu*varss.get(2).doubleValue(); });

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
