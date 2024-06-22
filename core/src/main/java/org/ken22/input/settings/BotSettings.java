package org.ken22.input.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ken22.input.odeinput.GridPathfindingType;

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

    public static void main(String[] args) {
        var settings = new BotSettings();
        settings.odesolverType = ODESolverType.RK4;
        settings.stepSize = 0.001;
        settings.differentiatorType = DifferentiatorType.THREE_POINT_CENTERED;
        settings.weightingType = WeightingType.EQUIVALENT;
        settings.errorFunctionType = ErrorFunctionType.EUCLIDEAN;
        settings.gridPathfindingType = GridPathfindingType.A_STAR;
        settings.randomRestarts = 10;

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
