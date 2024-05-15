package org.ken22.interfaces;

import org.ken22.odesolver.methods.SolverMethodType;

import java.util.ArrayList;
import java.util.HashMap;

public class UserInput {
    public final HashMap<String, Double> initialValuesMap;
    public final ArrayList<String> equations;
    public final double stepSize;
    public final double startTime;
    public final double endTime;
    public final boolean graph;
    public final boolean table;
    public final boolean phase;
    public final SolverMethodType methodType;

    public UserInput(ArrayList<String> equations, HashMap<String, Double> initialValuesMap, double stepSize, double startTime, double endTime, boolean graph, boolean table, boolean phase, SolverMethodType methodType) {
        this.initialValuesMap = initialValuesMap;
        this.equations = equations;
        this.stepSize = stepSize;
        this.startTime = startTime;
        this.endTime = endTime;
        this.graph = graph;
        this.table = table;
        this.phase = phase;
        this.methodType = methodType;
    }
}
