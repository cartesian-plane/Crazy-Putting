package interfaces;

import java.util.ArrayList;

public class ODESolution {
    ArrayList<Number> time;
    ArrayList<ArrayList<Number>> stateVectors;

    public ODESolution(ArrayList<Number> time, ArrayList<ArrayList<Number>> stateVectors) {
        this.time = time;
        this.stateVectors = stateVectors;
    }
}
