package org.ken22.input.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ken22.input.odeinput.GraphAlgorithmType;
import org.ken22.players.pathfinding.GridPathfinding;

public class BotSettings {
    @JsonProperty
    public BotType botType;
    @JsonProperty
    public ODESolverType odesolverType;
    @JsonProperty
    public DifferentiatorType differentiatorType;
    @JsonProperty
    public double stepSize;
    @JsonProperty
    public ErrorFunctionType errorFunctionType;
    @JsonProperty
    public WeightingType weightingType;
    @JsonProperty
    public GridPathfindingType gridPathfindingType;
    @JsonProperty
    public LocalSearchType localSearchType;
    @JsonProperty
    public GraphAlgorithmType graphAlgorithmType;
    @JsonProperty
    public int randomRestarts; // no. of random restarts for the hill climbing algorithm
}
