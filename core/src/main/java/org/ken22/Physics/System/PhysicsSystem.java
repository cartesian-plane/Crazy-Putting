package org.ken22.Physics.System;

import org.ken22.Physics.Vectors.GVec4;
import org.ken22.input.courseinput.CourseParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.interfaces.IFunc;

public class PhysicsSystem {
    private Map<String, Double> vars;
    private ArrayList<Double> initialStateVector;
    double timeStep = 0.01; //default
    private GVec4 initialState;
    private Expression terrain;
    private GolfCourse course;

    // parameter order (t,x,y,vx, vy, gradx, grady, height)

    public PhysicsSystem(GVec4 initialStateVector, File JSonfile) {
        this.timeStep = initialStateVector.getTimeStep();
        this.initialState = initialStateVector;
        CourseParser parser = new CourseParser(new File("project-1-2/assets/input/golf-course.json"));
        this.vars = parser.getVariables();
        this.terrain = parser.getExpression();
        this.course = parser.getCourse();
    }

    public ArrayList<String> getVariables() {
        return new ArrayList<>(vars.keySet());
    }

    public Map<String, Double> getMap() {
        return vars;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public GVec4 getInitialState() {
        return initialState;
    }

    public Expression getTerrain() {
        return terrain;
    }

    public GolfCourse getCourse() {
        return course;
    }

    public ArrayList<Double> getInitialStateVector() {
        return initialStateVector;
    }

}
