package interfaces;

import odesolver.ODESolver;
import odesolver.methods.ODESolverMethod;
import odesolver.methods.RungeKutta2;

import java.util.ArrayList;
import java.util.HashMap;

import input.ODESystemFactory;

public class ODESystemTestFactory {
    ODESystem system(HashMap<String, Integer> varOrder, ArrayList<Double> initialStateVector, ArrayList<IFunc<Double, Double>> functions) {
        return new ODESystem(varOrder, initialStateVector, functions);
    }

    public ODESystem LotkaVolterra(double initialX, double initialY, double alpha, double beta,
                                    double delta, double gamma) {

        if (alpha <= 0 || beta <= 0 || gamma <= 0 || delta <= 0) {
            throw new IllegalArgumentException("alpha, beta, gamma, and delta must be positive");
        }

        ArrayList<Double> initialStateVector = new ArrayList<>();
        ArrayList<IFunc<Double, Double>> functions = new ArrayList<>();

        // initial conditions
        ArrayList<Double> initialValues = new ArrayList<>();
        initialValues.add(initialX);
        initialValues.add(initialY);

        initialStateVector.add(initialValues.get(0));
        initialStateVector.add(initialValues.get(1));


        functions.add((systemVars) -> {
            return alpha * systemVars.get(0).doubleValue() - beta * systemVars.get(0).doubleValue()
                    * systemVars.get(1).doubleValue();
        });

        functions.add((systemVars) -> {
            return delta * systemVars.get(0).doubleValue() * systemVars.get(1).doubleValue() -
                    gamma * systemVars.get(1).doubleValue();
        });

        return new ODESystem(initialStateVector, functions);
    }

    public ODESystem LotkaVolterra(double initialX, double initialY) {

        double alpha = 1;
        double beta = 1;
        double delta = 1;
        double gamma = 1;
        ArrayList<Double> initialStateVector = new ArrayList<>();
        ArrayList<IFunc<Double, Double>> functions = new ArrayList<>();

        // initial conditions
        ArrayList<Double> initialValues = new ArrayList<>();
        initialValues.add(initialX);
        initialValues.add(initialY);

        initialStateVector.add(initialValues.get(0));
        initialStateVector.add(initialValues.get(1));


        functions.add((systemVars) -> {
            return alpha * systemVars.get(0).doubleValue() - beta * systemVars.get(0).doubleValue()
                    * systemVars.get(1).doubleValue();
        });

        functions.add((systemVars) -> {
            return delta * systemVars.get(0).doubleValue() * systemVars.get(1).doubleValue() -
                    gamma * systemVars.get(1).doubleValue();
        });

        return new ODESystem(initialStateVector, functions);
    }

    public ODESystem FitzHughNagumo() {
        ArrayList<Double> initialStateVector = new ArrayList<>();
        ArrayList<IFunc<Double, Double>> functions = new ArrayList<>();
        ArrayList<Double> vars = new ArrayList<>();
        vars.add(1.0);
        vars.add(0.0);
        initialStateVector.add(vars.get(0));
        functions.add((systemVars) -> {
            return systemVars.getFirst().doubleValue() - Math.pow(systemVars.getFirst().doubleValue(), 3) / 3 - systemVars.get(1).doubleValue() + 0.1;
        });
        functions.add((systemVars) -> {
            return 0.05 * (systemVars.getFirst().doubleValue() + 0.95 - 0.91 * systemVars.get(1).doubleValue() + 0.1);
        });
        return new ODESystem(initialStateVector, functions);
    }

    public ODESystem SIR(double initialS, double initialI, double initialR, double k, double mu, double gamma) {
        ArrayList<Double> initialStateVector = new ArrayList<>();
        ArrayList<IFunc<Double, Double>> functions = new ArrayList<>();

        // initial conditions
        ArrayList<Double> initialValues = new ArrayList<>();
        initialValues.add(initialS);
        initialValues.add(initialI);
        initialValues.add(initialR);

        initialStateVector.add(initialValues.get(0));
        initialStateVector.add(initialValues.get(1));
        initialStateVector.add(initialValues.get(2));


        functions.add((systemVars) -> {
            return -k * systemVars.get(0).doubleValue() * systemVars.get(1).doubleValue() +
                    mu * (1 - systemVars.get(0).doubleValue());
        });

        functions.add((systemVars) -> {
            return k * systemVars.get(0).doubleValue() * systemVars.get(1).doubleValue() -
                    (gamma + mu) * systemVars.get(1).doubleValue();
        });

        functions.add((systemVars) -> {
            return gamma * systemVars.get(1).doubleValue() - mu * systemVars.get(2).doubleValue();
        });

        return new ODESystem(initialStateVector, functions);

    }

    public ODESystem SIR(double initialS, double initialI, double initialR) {

        double k = 3;
        double mu = 0.001;
        double gamma = 2;

        ArrayList<Double> initialStateVector = new ArrayList<>();
        ArrayList<IFunc<Double, Double>> functions = new ArrayList<>();

        // initial conditions
        ArrayList<Double> initialValues = new ArrayList<>();
        initialValues.add(initialS);
        initialValues.add(initialI);
        initialValues.add(initialR);

        initialStateVector.add(initialValues.get(0));
        initialStateVector.add(initialValues.get(1));
        initialStateVector.add(initialValues.get(2));


        functions.add((systemVars) -> {
            return -k * systemVars.get(0).doubleValue() * systemVars.get(1).doubleValue() +
                    mu * (1 - systemVars.get(0).doubleValue());
        });

        functions.add((systemVars) -> {
            return k * systemVars.get(0).doubleValue() * systemVars.get(1).doubleValue() -
                    (gamma + mu) * systemVars.get(1).doubleValue();
        });

        functions.add((systemVars) -> {
            return gamma * systemVars.get(1).doubleValue() - mu * systemVars.get(2).doubleValue();
        });

        return new ODESystem(initialStateVector, functions);

    }


    public ODESystem testSyst() {
        ArrayList<Double> initialStateVector = new ArrayList<>();
        ArrayList<IFunc<Double, Double>> functions = new ArrayList<>();

        ArrayList<Double> vars = new ArrayList<>();
        vars.add(1.0);

        initialStateVector.add(vars.get(0));

        functions.add((varss) -> {
            return 2 * varss.get(0).doubleValue();
        });

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
        HashMap<String, Double> in = new HashMap<>();
        in.put("x", 10.0);
        in.put("y", 20.0);
        ODESystemFactory gen = new ODESystemFactory(in, ex);
        gen.getSyst();
        ODESolver solv = new ODESolver();
        ODESolverMethod stra = new RungeKutta2(gen.getSyst(), 0.000001, 0, 5);
        solv.setStrategy(stra);
        System.out.println(solv.solve());
    }
}
