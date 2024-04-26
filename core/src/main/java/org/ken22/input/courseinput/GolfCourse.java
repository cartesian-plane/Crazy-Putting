package org.ken22.input.courseinput;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GolfCourse {
    @JsonProperty("name")
    private String name;

    @JsonProperty("courseProfile")
    private String courseProfile;

    @JsonProperty("mass")
    private double mass;

    @JsonProperty("gravitationalConstant")
    private double gravitationalConstant;

    @JsonProperty("kineticFrictionGrass")
    private double kineticFrictionGrass;

    @JsonProperty("staticFrictionGrass")
    private double staticFrictionGrass;

    @JsonProperty("kineticFrictionSand")
    private double kineticFrictionSand;

    @JsonProperty("staticFrictionSand")
    private double staticFrictionSand;

    @JsonProperty("maximumSpeed")
    private double maximumSpeed;

    @JsonProperty("targetRadius")
    private double targetRadius;

    public double getMass() {
        return mass;
    }

    public String getCourseProfile() {
        return courseProfile;
    }
}
