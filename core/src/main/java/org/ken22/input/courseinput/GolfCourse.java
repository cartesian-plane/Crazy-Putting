package org.ken22.input.courseinput;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GolfCourse(
    @JsonProperty("name") String name,
    @JsonProperty("courseProfile") String courseProfile,
    @JsonProperty("range") double range,
    @JsonProperty("mass") double mass,
    @JsonProperty("gravitationalConstant") double gravitationalConstant,
    @JsonProperty("kineticFrictionGrass") double kineticFrictionGrass,
    @JsonProperty("staticFrictionGrass") double staticFrictionGrass,
    @JsonProperty("kineticFrictionSand") double kineticFrictionSand,
    @JsonProperty("staticFrictionSand") double staticFrictionSand,
    @JsonProperty("maximumSpeed") double maximumSpeed,
    @JsonProperty("targetRadius") double targetRadius,
    @JsonProperty("targetXcoord") double targetXcoord,
    @JsonProperty("targetYcoord") double targetYcoord
) {}
