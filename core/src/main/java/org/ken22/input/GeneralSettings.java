package org.ken22.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Simple class to store the settings chosen by the user.
 * <p>Due to serializing limitations, the {@link org.ken22.physics.odesolvers.ODESolver} and
 * {@link org.ken22.physics.differentiators.Differentiator} are stored as strings, and need to be handled somewhere
 * else in the code.</p>
 */
public class GeneralSettings {
    @JsonProperty("solver")
    public String solver;
    @JsonProperty("stepSize")
    public double stepSize;
    @JsonProperty("Differentiator")
    public String differentiator;
    @JsonProperty("differentiationStepSize")
    public double differentiationStepSize;
    @JsonProperty("simplifiedPhysics")
    public boolean useSimplifiedPhysics;
    @JsonProperty("allowPlaying")
    public boolean allowPlaying;
}
