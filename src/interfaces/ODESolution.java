package interfaces;

import java.util.ArrayList;

public class ODESolution {
    ArrayList<Double> time;
    ArrayList<ArrayList<Double>> stateVectors;
    /**
     * The time taken (in ms) to compute the solution.
     */
    private long timeTaken;

    public ODESolution(ArrayList<Double> time, ArrayList<ArrayList<Double>> stateVectors) {
        this.time = time;
        this.stateVectors = stateVectors;
    }

    public ArrayList<ArrayList<Double>> getStateVectors() {
        return stateVectors;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public ArrayList<Double> getTime() {
        return time;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    @Override
    public String toString() {
        String s = "Time taken: " + timeTaken + "\n";
        s += "Final state: " + stateVectors.get(stateVectors.size()-1) + "";
        return s;
    }
}
