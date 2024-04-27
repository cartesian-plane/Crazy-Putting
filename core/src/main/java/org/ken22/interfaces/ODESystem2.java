package org.ken22.interfaces;

import org.ken22.Physics.Vectors.GVec4;
import org.ken22.input.courseinput.CourseParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ODESystem2 {
    ArrayList<Double> initialStateVector;
    ArrayList<IFunc<Double, Double>> functions;
    HashMap<String, Integer> varOrder;

    // parameter order (t,x,y,vx, vy, gradx, grady, height)
    private IFunc<Double, Double> f_ax = (vars) ->
        ( -1*this.gCoef*(vars.get(5)+this.kFrictionCoef*vars.get(3) /
            (Math.sqrt( Math.pow(vars.get(3),2)+Math.pow(vars.get(4),2)))));
    private IFunc<Double, Double> f_ay = (vars) ->
        ( -1*this.gCoef*(vars.get(6)+this.kFrictionCoef*vars.get(4) /
            (Math.sqrt(Math.pow(vars.get(3),2)+Math.pow(vars.get(4),2)))));;

    public ODESystem(GVec4 initialStateVector, File JSonfile) {
        if (initialStateVector.size() != functions.size()) {
            throw new IllegalArgumentException("The size of the state vector must be equal to the number of functions.");
        }
        this.initialStateVector = initialStateVector;
        this.functions = functions;
        this.varOrder = varOrder;
        this.timeStep = initialStateVector.getTimeStep();
        this.endTime = initialStateVector.getEndTime();
        this.initialState = initialStateVector;
        CourseParser parser = new CourseParser(new File("project-1-2/assets/input/golf-course.json"));
        this.terrain = parser.getExpression();
        this.course = parser.getCourse();
        this.solver = solver;
    }

    public ArrayList<String> getVariables() {
        return new ArrayList<>(varOrder.keySet());
    }

    //untested
    public ArrayList<Double> derivative() {
        ArrayList<Double> derivative = new ArrayList<>();
        for (int i = 0; i < initialStateVector.size(); i++) {
            derivative.add(functions.get(i).apply(initialStateVector));
        }
        return derivative;
    }

    public ArrayList<Double> derivative(ArrayList<Double> stateVector) {
        if (stateVector.size() != initialStateVector.size()) {
            throw new IllegalArgumentException("The size of the state vector must be equal to the number of functions.");
        }
        ArrayList<Double> derivative = new ArrayList<>();
        for (int i = 0; i < stateVector.size(); i++) {
            derivative.add(functions.get(i).apply(stateVector));
        }
        return derivative;
    }

    @Override
    public String toString() {
        return "ODESystem{" +
            "initialStateVector=" + initialStateVector +
            // ", functions=" + functions +
            '}';
    }

    public ArrayList<Double> getInitialStateVector() {
        return initialStateVector;
    }

    public ArrayList<IFunc<Double, Double>> getFunctions() {
        return functions;
    }
}
