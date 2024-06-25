package org.ken22.input.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BotSettings {
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
    public int randomRestarts; // no. of random restarts for the hill climbing algorithm
    @JsonProperty
    public int sidewaysMoves; // no. of sideways moves for the hill climbing algorithm


    // Hill Climbing
    @JsonProperty
    public int hcMaxIterations;
    @JsonProperty
    public double hcErrorThreshold;
    @JsonProperty
    public double hcConvergenceThreshold;
    @JsonProperty
    public double hcStepSize;

    // Newton-Raphson
    @JsonProperty
    public int  nrMaxIterations;
    @JsonProperty
    public double nrTolerance;
    @JsonProperty
    public double nrErrorThreshold;

    // Simulated Annealing
    @JsonProperty
    public double saInitialTemperature;
    @JsonProperty
    public double saCoolingRate;
    @JsonProperty
    public int saMaxIterations;
    @JsonProperty
    public double saDelta;

    // Gradient Descent
    @JsonProperty
    public double gdDelta;
    @JsonProperty
    public double gdThreshold;
    @JsonProperty
    public int gdMaxSidewaysMoves;
    @JsonProperty
    public int gdMaxRestarts;



    public static void main(String[] args) {
        var settings = new BotSettings();
        settings.odesolverType = ODESolverType.RK4;
        settings.stepSize = 0.001;
        settings.differentiatorType = DifferentiatorType.THREE_POINT_CENTERED;
        settings.weightingType = WeightingType.EQUIVALENT;
        settings.errorFunctionType = ErrorFunctionType.EUCLIDEAN;
        settings.gridPathfindingType = GridPathfindingType.A_STAR;
        settings.localSearchType = LocalSearchType.NONE;
        settings.randomRestarts = 9;
        settings.sidewaysMoves = 10;
        settings.hcMaxIterations = 500;
        settings.hcErrorThreshold = 0.15;
        settings.hcConvergenceThreshold = 0.001;
        settings.hcStepSize = 0.05;
        settings.nrMaxIterations = 250;
        settings.nrTolerance = 1e-6;
        settings.nrErrorThreshold = 0.05;
        settings.saInitialTemperature = 1000;
        settings.saCoolingRate = 0.5;
        settings.saMaxIterations = 1000;
        settings.saDelta = 0.05;
        settings.gdDelta = 0.01;
        settings.gdThreshold = 1.0;
        settings.gdMaxSidewaysMoves = 10;
        settings.gdMaxRestarts = 10;

        // serialize this object to JSON with pretty printer
        var objectMapper = new ObjectMapper();
        var json = "";
        try {
            json = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(settings);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String filePath = "assets/input/settings/default-bot-settings.json"; // replace with your file path

        try {
            Files.write(Paths.get(filePath), json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
