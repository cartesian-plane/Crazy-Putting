package interfaces;

import java.util.ArrayList;

public class ODESystem {
    ArrayList<Number> initialStateVector;
    ArrayList<IFunc<Number, Number>> functions;

    public ODESystem(ArrayList<Number> initialStateVector, ArrayList<IFunc<Number, Number>> functions) {
        if (initialStateVector.size() != functions.size()) {
            throw new IllegalArgumentException("The size of the state vector must be equal to the number of functions.");
        }
        this.initialStateVector = initialStateVector;
        this.functions = functions;
    }
}
