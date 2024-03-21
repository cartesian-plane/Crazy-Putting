package interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ODESystem {
    ArrayList<Double> initialStateVector;
    ArrayList<IFunc<Double, Double>> functions;
    HashMap<String, Integer> varOrder;

    public ODESystem(HashMap<String, Integer> varOrder, ArrayList<Double> initialStateVector, ArrayList<IFunc<Double, Double>> functions) {
        if (initialStateVector.size() != functions.size()) {
            throw new IllegalArgumentException("The size of the state vector must be equal to the number of functions.");
        }
        this.initialStateVector = initialStateVector;
        this.functions = functions;
        this.varOrder = varOrder;
    }

    public ArrayList<String> getVariables() {
        return new ArrayList<>(varOrder.keySet());
    }

    //untested
    public ArrayList<Double> derivative() {
        ArrayList<Double> derivative = new ArrayList<>();
        for (int i = 0; i < initialStateVector.size(); i++) {
            derivative.add(functions.get(i).apply(initialStateVector));
        }
        return derivative;
    }

    public ArrayList<Double> derivative(ArrayList<Double> stateVector) {
        if (stateVector.size() != initialStateVector.size()) {
            throw new IllegalArgumentException("The size of the state vector must be equal to the number of functions.");
        }
        ArrayList<Double> derivative = new ArrayList<>();
        for (int i = 0; i < stateVector.size(); i++) {
            derivative.add(functions.get(i).apply(stateVector));
        }
        return derivative;
    }

    @Override
    public String toString() {
        return "ODESystem{" +
                "initialStateVector=" + initialStateVector +
                // ", functions=" + functions +
                '}';
    }

    public ArrayList<Double> getInitialStateVector() {
        return initialStateVector;
    }

    public ArrayList<IFunc<Double, Double>> getFunctions() {
        return functions;
    }
}
