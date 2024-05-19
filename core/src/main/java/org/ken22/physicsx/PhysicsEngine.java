package org.ken22.physicsx;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physicsx.differentiation.Differentiator;
import org.ken22.physicsx.odesolvers.ODESolver;
import org.ken22.physicsx.vectors.StateVector4;

import java.util.ArrayList;
import java.util.Iterator;

public class PhysicsEngine {

    private GolfCourse course;
    private StateVector4 initialStateVector;
    private double timeStep;
    private Differentiator differentiator;
    private ODESolver solver;
    // public Iterator frameIterator implements Iterator;
    private ArrayList<StateVector4> trajectory;



    public PhysicsEngine(GolfCourse course, StateVector4 initialStateVector) {
        this.course = course;
        this.initialStateVector = initialStateVector;
        this.timeStep = 0.0001;
        trajectory.add(initialStateVector);
    }

    public PhysicsEngine(GolfCourse course, StateVector4 initialStateVector, double timeStep,
                         Differentiator differentiator, ODESolver solver) {

        if (timeStep < 0.016) {
            throw new IllegalArgumentException("Step size too big for 60FPS");
        }

        this.course = course;
        this.initialStateVector = initialStateVector;
        this.timeStep = timeStep;
        this.differentiator = differentiator;
        this.solver = solver;
        trajectory.add(initialStateVector);
    }

    public boolean isAtRest() {
        return true;
    }

    private StateVector4 nextStep() {
        StateVector4 lastVector = trajectory.getLast();

        // solver.nextStep(timeStep, lastVector, Vecto);
        return null;
    }
}
