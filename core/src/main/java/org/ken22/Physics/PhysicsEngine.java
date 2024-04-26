package org.ken22.Physics;

import org.ken22.interfaces.IFunc;

import java.util.ArrayList;

public class PhysicsEngine {
    //The only redeeming quality of this class is that it does what it's supposed to do

    private double timeStep;
    private double endTime;
    private double startTime;
    private double kFrictionCoef;
    private double sFrictionCoef;
    private double gCoef;
    private String name;
    private IFunc<Double, Double> height; //Parameters are (x,y), passed in constructor

    // parameter order (t,x,y,vx, vy, gradx, grady, height)
    private IFunc<Double, Double> f_ax = (vars) ->
        ( -1*this.gCoef*(vars.get(5)+this.kFrictionCoef*vars.get(3)/( Math.sqrt( Math.pow(vars.get(3),2)+Math.pow(vars.get(4),2) ) )));
    private IFunc<Double, Double> f_ay = (vars) ->
        ( -1*this.gCoef*(vars.get(6)+this.kFrictionCoef*vars.get(4)/(Math.sqrt(Math.pow(vars.get(3),2)+Math.pow(vars.get(4),2)))));;
    private ArrayList<Double> initialState = new ArrayList<Double>(); // (t,x,y,vx, vy, gradX, gradY), gradX and gradY for initial state are calculated in the loop
    private ArrayList<ArrayList<Double>> stateVectors = new ArrayList<ArrayList<Double>>(); // (t,x,y,vx, vy, gradX, gradY)


    public String getName() {
        return name;
    }

    public PhysicsEngine(double x, double y, double vx, double vy, double timeStep, double startTime, double endTime, double kFrictionCoef, double sFrictionCoef, double gCoef, IFunc<Double, Double> height, String name) {
        this.timeStep = timeStep;
        this.endTime = endTime;
        this.kFrictionCoef = kFrictionCoef;
        this.sFrictionCoef = sFrictionCoef;
        this.gCoef = gCoef;
        this.height = height;
        initialState.add(startTime);
        initialState.add(x);
        initialState.add(y);
        initialState.add(vx);
        initialState.add(vy);
        this.name = name;
    }

    public void solve() {

        ArrayList<Double> current = initialState;
        double t = initialState.get(0);
        while(t < this.endTime) {
            // Calculate gradients in x and y directions
            ArrayList<Double> gradients = this.gradients(current, this.height, this.timeStep);
            // Temporarily store gradients in current vector
            current.add(gradients.get(0)); //Gradient in x direction
            current.add(gradients.get(1)); //Gradient in y direction
            ArrayList<Double> xy = new ArrayList<Double>();
            xy.add(current.get(1));
            xy.add(current.get(2));
            current.add(height.apply(xy)); //Height at current position (x,y)
            double ax = f_ax.apply(current);
            double ay = f_ay.apply(current);

            updateState(current, ax, ay); //Forward euler?
            current = stateVectors.getLast();
            t += this.timeStep;
        }
    }

    private void updateState(ArrayList<Double> current, double ax, double ay) {
        ArrayList<Double> newState = new ArrayList<Double>();

        double newTime = current.get(0) + timeStep;
        newState.add(newTime);

        double newX = current.get(1) + current.get(3)*timeStep;
        double newY = current.get(2) + current.get(4)*timeStep;
        newState.add(newX);
        newState.add(newY);

        double newVx = current.get(3) + ax*timeStep;
        double newVy = current.get(4) + ay*timeStep;
        newState.add(newVx);
        newState.add(newVy);

        this.stateVectors.add(newState);
    }

    /**
     * Approximate gradient of function at current point
     * @param stateVector
     * @param function
     * @param timeStep
     * @return
     */
    public ArrayList<Double> gradients(ArrayList<Double> stateVector, IFunc<Double, Double> function, double timeStep) {

        ArrayList<Double> gradients = new ArrayList<Double>();
        ArrayList<Double> coords = new ArrayList<Double>(); // {x,y}
        coords.add(stateVector.get(1));
        coords.add(stateVector.get(2));

        //With respect to x
        double delX = stateVector.get(3) * this.timeStep;
        ArrayList<Double> coordsX = new ArrayList<Double>();
        coordsX.add(coords.get(0)+delX);
        coordsX.add(coords.get(1));
        double dhdx = (height.apply(coordsX) - height.apply(coords))/delX;
        gradients.add(dhdx);

        //With respect to y
        double delY = stateVector.get(4) * this.timeStep;
        ArrayList<Double> coordsY = new ArrayList<Double>();
        coordsY.add(coords.get(0));
        coordsY.add(coords.get(1)+delY);
        double dhdy = (height.apply(coordsY) - height.apply(coords))/delY;
        gradients.add(dhdy);

        return gradients;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public double getEndTime() {
        return endTime;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getkFrictionCoef() {
        return kFrictionCoef;
    }

    public double getsFrictionCoef() {
        return sFrictionCoef;
    }

    public double getgCoef() {
        return gCoef;
    }

    public IFunc<Double, Double> getHeight() {
        return height;
    }

    public IFunc<Double, Double> getF_ax() {
        return f_ax;
    }

    public IFunc<Double, Double> getF_ay() {
        return f_ay;
    }

    public ArrayList<Double> getInitialState() {
        return initialState;
    }

    public ArrayList<ArrayList<Double>> getStateVectors() {
        return stateVectors;
    }


}
