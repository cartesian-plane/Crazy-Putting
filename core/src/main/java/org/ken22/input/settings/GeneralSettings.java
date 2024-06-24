package org.ken22.input.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ken22.physics.odesolvers.outofplace.ODESolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Simple class to store the settings chosen by the user.
 * <p>Due to serializing limitations, the {@link ODESolver} and
 * {@link org.ken22.physics.differentiators.Differentiator} are stored as strings, and need to be handled somewhere
 * else in the code.</p>
 */
public class GeneralSettings {
    @JsonProperty("solver")
    public ODESolverType solverType;
    @JsonProperty("stepSize")
    public double stepSize;
    @JsonProperty("Differentiator")
    public DifferentiatorType differentiatorType;
    @JsonProperty("simplifiedPhysics")
    public boolean useSimplifiedPhysics;
    @JsonProperty("allowPlaying")
    public boolean allowPlaying;

    public static void main(String[] args) {
        var settings = new GeneralSettings();
        settings.solverType = ODESolverType.RK4;
        settings.stepSize = 0.001;
        settings.differentiatorType = DifferentiatorType.THREE_POINT_CENTERED;
        settings.useSimplifiedPhysics = true;
        settings.allowPlaying = true;

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
        String filePath = "assets/input/settings/default-settings.json"; // replace with your file path

        try {
            Files.write(Paths.get(filePath), json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public GeneralSettings() {
    }

    public GeneralSettings(ODESolverType odeSolverType, double stepSize, DifferentiatorType differentiatorType, boolean useSimplifiedPhysics, boolean allowPlaying) {
        this.solverType = odeSolverType;
        this.stepSize = stepSize;
        this.differentiatorType = differentiatorType;
        this.useSimplifiedPhysics = useSimplifiedPhysics;
        this.allowPlaying = allowPlaying;
    }
}
