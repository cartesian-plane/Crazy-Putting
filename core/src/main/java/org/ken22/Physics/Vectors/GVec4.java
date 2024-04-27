package org.ken22.Physics.Vectors;

public class GVec4 {

    double[] initialState = new double[8];
    double timeStep;
    double endTime;

    public GVec4(double t0, double x0, double y0, double vx0, double vy0, double timeStep, double endTime) {
        initialState[0] = t0;
        initialState[1] = x0;
        initialState[2] = y0;
        initialState[3] = vx0;
        initialState[4] = vy0;
        this.timeStep = timeStep;
        this.endTime = endTime;
    }

    public int size() {
        return initialState.length;
    }

    public double getT0() {
        return initialState[0];
    }

    public double getX0() {
        return initialState[1];
    }

    public double getY0() {
        return initialState[2];
    }

    public double getVx0() {
        return initialState[3];
    }

    public double getVy0() {
        return initialState[4];
    }

    public double getTimeStep() {
        return timeStep;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setT0(double t0) {
        this.initialState[0] = t0;
    }

    public void setX0(double x0) {
        this.initialState[1] = x0;
    }

    public void setY0(double y0) {
        this.initialState[2] = y0;
    }

    public void setVx0(double vx0) {
        this.initialState[3] = vx0;
    }

    public void setVy0(double vy0) {
        this.initialState[4] = vy0;
    }

    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }
}
