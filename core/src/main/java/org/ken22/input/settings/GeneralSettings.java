package org.ken22.input.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simple class to store the settings chosen by the user.
 * <p>Due to serializing limitations, the {@link org.ken22.physics.odesolvers.ODESolver} and
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
}
