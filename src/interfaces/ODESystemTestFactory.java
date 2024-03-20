package interfaces;

import odesolver.ODESolver;
import odesolver.methods.EulerMethod;
import odesolver.methods.ODESolverMethod;
import odesolver.methods.RungeKutta2;

import java.util.ArrayList;

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
        ODESystemTestFactory factory = new ODESystemTestFactory();
        ODESystem syst = factory.testSyst();
        System.out.println(syst + "\n" + syst.derivative());

        ODESolver solverContext = new ODESolver();
        ODESolverMethod strat = new RungeKutta4(syst, 0.00001, 0, 100);
        solverContext.setStrategy(strat);
        ODESolution solution = solverContext.solve();
        System.out.println("Initial state vector");
        double expected = Math.exp(2);
        System.out.println("Expected value: " + expected);
        System.out.println("Absolute difference: " + Math.abs(solution.stateVectors.get(100000).getFirst().doubleValue() - expected));
        System.out.println(solution.stateVectors.get(100000));

    }
}
