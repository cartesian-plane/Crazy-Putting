package interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ODESystem {
    ArrayList<Number> initialStateVector;
    ArrayList<IFunc<Number, Number>> functions;

    HashSet<String> variables;
    HashMap<String, Integer> variableIndices;

    public ODESystem(ArrayList<Number> initialStateVector, ArrayList<IFunc<Number, Number>> functions) {
        if (initialStateVector.size() != functions.size()) {
            throw new IllegalArgumentException("The size of the state vector must be equal to the number of functions.");
        }
        this.initialStateVector = initialStateVector;
        this.functions = functions;
    }

    @Deprecated
    public ArrayList<Number> derivative() {
        ArrayList<Number> derivative = new ArrayList<>();
        for (int i = 0; i < initialStateVector.size(); i++) {
            derivative.add(functions.get(i).apply(initialStateVector));
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

    public ArrayList<Number> getInitialStateVector() {
        return initialStateVector;
    }

    public ArrayList<IFunc<Number, Number>> getFunctions() {
        return functions;
    }
}
