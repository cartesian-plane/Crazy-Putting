package org.ken22.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ken22.input.odeinput.GraphAlgorithmType;

public class BotSettings {
    @JsonProperty
    public BotType botType;
    @JsonProperty
    public LocalSearchType localSearchType;
    @JsonProperty
    public GraphAlgorithmType graphAlgorithmType;
    @JsonProperty
    public int randomRestarts; // no. of random restarts for the hill climbing algorithm
}
