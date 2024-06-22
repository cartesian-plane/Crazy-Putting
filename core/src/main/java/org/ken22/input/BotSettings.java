package org.ken22.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ken22.input.odeinput.GraphAlgorithmType;

import java.io.File;
import java.io.IOException;

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
