package org.ken22.input.courseinput;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ken22.obstacles.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public  class GolfCourse {
    @JsonProperty("name")
    public String name;
    @JsonProperty("courseProfile")
    public String courseProfile;
    @JsonProperty("range")
    public double range;
    @JsonProperty("mass")
    public double mass;
    @JsonProperty("gravitationalConstant")
    public double gravitationalConstant;
    @JsonProperty("kineticFrictionGrass")
    public double kineticFrictionGrass;
    @JsonProperty("staticFrictionGrass")
    public double staticFrictionGrass;
    @JsonProperty("kineticFrictionSand")
    public double kineticFrictionSand;
    @JsonProperty("staticFrictionSand")
    public double staticFrictionSand;
    @JsonProperty("maximumSpeed")
    public double maximumSpeed;
    @JsonProperty("targetRadius")
    public double targetRadius;
    @JsonProperty("targetXcoord")
    public double targetXcoord;
    @JsonProperty("targetYcoord")
    public double targetYcoord;
    @JsonProperty("ballXcoord")
    public double ballX;
    @JsonProperty("ballYcoord")
    public double ballY;
    // obstacles

    public List<Tree> trees = new ArrayList<>();

    public GolfCourse(
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
        @JsonProperty("targetYcoord") double targetYcoord,
        @JsonProperty("ballXcoord") double ballX,
        @JsonProperty("ballYcoord") double ballY
    ) {
        this.name = name;
        this.courseProfile = courseProfile;
        this.range = range;
        this.mass = mass;
        this.gravitationalConstant = gravitationalConstant;
        this.kineticFrictionGrass = kineticFrictionGrass;
        this.staticFrictionGrass = staticFrictionGrass;
        this.kineticFrictionSand = kineticFrictionSand;
        this.staticFrictionSand = staticFrictionSand;
        this.maximumSpeed = maximumSpeed;
        this.targetRadius = targetRadius;
        this.targetXcoord = targetXcoord;
        this.targetYcoord = targetYcoord;
        this.ballX = ballX;
        this.ballY = ballY;
    }

    @JsonProperty("name")
    public String name() {
        return name;
    }

    @JsonProperty("courseProfile")
    public String courseProfile() {
        return courseProfile;
    }

    @JsonProperty("range")
    public double range() {
        return range;
    }

    @JsonProperty("mass")
    public double mass() {
        return mass;
    }

    @JsonProperty("gravitationalConstant")
    public double gravitationalConstant() {
        return gravitationalConstant;
    }

    @JsonProperty("kineticFrictionGrass")
    public double kineticFrictionGrass() {
        return kineticFrictionGrass;
    }

    @JsonProperty("staticFrictionGrass")
    public double staticFrictionGrass() {
        return staticFrictionGrass;
    }

    @JsonProperty("kineticFrictionSand")
    public double kineticFrictionSand() {
        return kineticFrictionSand;
    }

    @JsonProperty("staticFrictionSand")
    public double staticFrictionSand() {
        return staticFrictionSand;
    }

    @JsonProperty("maximumSpeed")
    public double maximumSpeed() {
        return maximumSpeed;
    }

    @JsonProperty("targetRadius")
    public double targetRadius() {
        return targetRadius;
    }

    @JsonProperty("targetXcoord")
    public double targetXcoord() {
        return targetXcoord;
    }

    @JsonProperty("targetYcoord")
    public double targetYcoord() {
        return targetYcoord;
    }

    @JsonProperty("ballXcoord")
    public double ballX() {
        return ballX;
    }

    @JsonProperty("ballYcoord")
    public double ballY() {
        return ballY;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GolfCourse) obj;
        return Objects.equals(this.name, that.name) &&
            Objects.equals(this.courseProfile, that.courseProfile) &&
            Double.doubleToLongBits(this.range) == Double.doubleToLongBits(that.range) &&
            Double.doubleToLongBits(this.mass) == Double.doubleToLongBits(that.mass) &&
            Double.doubleToLongBits(this.gravitationalConstant) == Double.doubleToLongBits(that.gravitationalConstant) &&
            Double.doubleToLongBits(this.kineticFrictionGrass) == Double.doubleToLongBits(that.kineticFrictionGrass) &&
            Double.doubleToLongBits(this.staticFrictionGrass) == Double.doubleToLongBits(that.staticFrictionGrass) &&
            Double.doubleToLongBits(this.kineticFrictionSand) == Double.doubleToLongBits(that.kineticFrictionSand) &&
            Double.doubleToLongBits(this.staticFrictionSand) == Double.doubleToLongBits(that.staticFrictionSand) &&
            Double.doubleToLongBits(this.maximumSpeed) == Double.doubleToLongBits(that.maximumSpeed) &&
            Double.doubleToLongBits(this.targetRadius) == Double.doubleToLongBits(that.targetRadius) &&
            Double.doubleToLongBits(this.targetXcoord) == Double.doubleToLongBits(that.targetXcoord) &&
            Double.doubleToLongBits(this.targetYcoord) == Double.doubleToLongBits(that.targetYcoord) &&
            Double.doubleToLongBits(this.ballX) == Double.doubleToLongBits(that.ballX) &&
            Double.doubleToLongBits(this.ballY) == Double.doubleToLongBits(that.ballY);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, courseProfile, range, mass, gravitationalConstant, kineticFrictionGrass, staticFrictionGrass, kineticFrictionSand, staticFrictionSand, maximumSpeed, targetRadius, targetXcoord, targetYcoord, ballX, ballY);
    }

    @Override
    public String toString() {
        return "GolfCourse[" +
            "name=" + name + ", " +
            "courseProfile=" + courseProfile + ", " +
            "range=" + range + ", " +
            "mass=" + mass + ", " +
            "gravitationalConstant=" + gravitationalConstant + ", " +
            "kineticFrictionGrass=" + kineticFrictionGrass + ", " +
            "staticFrictionGrass=" + staticFrictionGrass + ", " +
            "kineticFrictionSand=" + kineticFrictionSand + ", " +
            "staticFrictionSand=" + staticFrictionSand + ", " +
            "maximumSpeed=" + maximumSpeed + ", " +
            "targetRadius=" + targetRadius + ", " +
            "targetXcoord=" + targetXcoord + ", " +
            "targetYcoord=" + targetYcoord + ", " +
            "ballX=" + ballX + ", " +
            "ballY=" + ballY + ']';
    }
}
