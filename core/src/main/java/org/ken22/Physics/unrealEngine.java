package org.ken22.Physics;

import net.objecthunter.exp4j.Expression;
import org.ken22.Physics.Vectors.GVec4;
import org.ken22.Physics.odesolver.ODESolver;
import org.ken22.Physics.odesolver.methods.ODESolverMethod;
import org.ken22.input.courseinput.CourseParser;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.interfaces.IFunc;

import java.io.File;
import java.util.ArrayList;

public class unrealEngine {
    private double timeStep;
    private double endTime;
    private GolfCourse course;
    private String name;

    private ODESolverMethod solver;
    private Expression terrain; //Parameters are (x,y), passed in constructor



    private GVec4 initialState; // (t,x,y,vx, vy, gradX, gradY), gradX and gradY for initial state are calculated in the loop
    private ArrayList<ArrayList<Double>> stateVectors = new ArrayList<ArrayList<Double>>(); // (t,x,y,vx, vy, gradX, gradY)

    public unrealEngine(ODESolverMethod solver, ) {

    }




}
