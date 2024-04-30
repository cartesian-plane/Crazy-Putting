package org.ken22.Physics.Engine;

import net.objecthunter.exp4j.Expression;
import org.ken22.Physics.Numerical_Derivation.NumDerivationMethod;
import org.ken22.Physics.Numerical_Derivation.basicDerivation;
import org.ken22.Physics.Numerical_Integration.NumIntegrationMethod;
import org.ken22.Physics.System.PhysicsSystem;
import org.ken22.Physics.Vectors.GVec4;
import org.ken22.Physics.mathTools.myMath;
import org.ken22.Physics.odesolver.ODESolver;
import org.ken22.Physics.odesolver.methods.ODESolverMethod;
import org.ken22.input.courseinput.CourseParser;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.interfaces.IFunc;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class unrealEngine {

    // parameter order (t,x,y,vx, vy, gradx, grady, height)
    private IFunc<Double, Double> ax_k_gr = (vars) ->
        ( -1*this.g*(vars.get(5)+this.kf_gr*vars.get(3) /
            myMath.pythagoras(vars.get(5), vars.get(6)) ));
    private IFunc<Double, Double> ay_k_gr = (vars) ->
        ( -1*this.g*(vars.get(6)+this.kf_gr*vars.get(4) /
            myMath.pythagoras(vars.get(5), vars.get(6)) ));;
    private IFunc<Double, Double> ax_s_gr = (vars) ->
        ( -1*this.g*(vars.get(5)+this.sf_gr*vars.get(5) /
            myMath.pythagoras(vars.get(5), vars.get(6)) ));
    private IFunc<Double, Double> ay_s_gr = (vars) ->
        ( -1*this.g*(vars.get(6)+this.sf_gr*vars.get(6) /
            myMath.pythagoras(vars.get(5), vars.get(6))));


    private IFunc<Double, Double> ax_k_sa = (vars) ->
        ( -1*this.g*(vars.get(5)+this.kf_sa*vars.get(3) /
            myMath.pythagoras(vars.get(3), vars.get(4)) ));
    private IFunc<Double, Double> ay_k_sa = (vars) ->
        ( -1*this.g*(vars.get(6)+this.kf_sa*vars.get(4) /
            myMath.pythagoras(vars.get(3), vars.get(4)) ));;
    private IFunc<Double, Double> ax_s_sa = (vars) ->
        ( -1*this.g*(vars.get(5)+this.sf_sa*vars.get(5) /
            myMath.pythagoras(vars.get(5), vars.get(6))) );
    private IFunc<Double, Double> ay_s_sa = (vars) ->
        ( -1*this.g*(vars.get(6)+this.sf_sa*vars.get(6) /
            myMath.pythagoras(vars.get(5), vars.get(6))));;

    //Coefficents
    private double kf_gr;
    private double sf_gr;
    private double g;
    private double sf_sa;
    private double kf_sa;
    private double maxspeed;
    //System
    private Map<String, Double> vars;
    private GolfCourse course;
    private String name;
    private double timeStep;
    private NumIntegrationMethod integrator;
    private NumDerivationMethod differentiator;
    private Expression terrain; //Parameters are (x,y), passed in constructor
    private GVec4 initialState; // (t,x,y,vx, vy)
    private ArrayList<GVec4> stateVectors = new ArrayList<GVec4>(); // (t,x,y,vx, vy, gradX, gradY)
    public unrealEngine(PhysicsSystem system, NumIntegrationMethod integrator, NumDerivationMethod differentiator) {
        this.vars = system.getMap();
        this.timeStep = system.getTimeStep();
        this.course = system.getCourse();
        this.integrator = integrator;
        this.differentiator = differentiator;
        this.terrain = system.getTerrain();
        this.maxspeed = system.getCourse().getMaximumSpeed();
        this.initialState = system.getInitialState();
        this.stateVectors.add(initialState);
    }

    public Expression getTerrain() {
        return terrain;
    }

    public GVec4 getInitialState() {
        return initialState;
    }

    public ArrayList<GVec4> getStateVectors() {
        return stateVectors;
    }

    public void run() {

        GVec4 current = initialState;
        boolean atRest = false;

        //do not allow initial velocity above threshold
        current.set(3, Math.min(current.get(3), this.maxspeed));
        current.set(4, Math.min(current.get(4), this.maxspeed));

        while(!atRest) { //stop when target is reached

            //calculates partial derivatives and adds them to current vector
            differentiator.gradients(current, this.terrain, this.timeStep);

            //Check if on grass or sand

            //GVec4 stateVector, double timeStep, IFunc<Double, Double> funcx, IFunc<Double, Double> funcy, Expression height, NumDerivationMethod differentiator
            if(current.get(3) < 0.05 && current.get(4) < 0.05) { //speed gets too low, 0.05 is threshold
                if(current.get(5) < 0.05 && current.get(6) < 0.05) { //flat surface
                    atRest = true;
                    integrator.execute(current, this.timeStep, ax_k_gr, ay_k_gr, this.terrain, this.differentiator);
                    //Is it possible to simply break?
                }

                else { //inclined surface
                    if(myMath.pythagoras(current.get(5), current.get(6)) < sf_gr) { //ball stops
                        atRest = true;
                        integrator.execute(current, this.timeStep, ax_s_gr, ay_s_gr, this.terrain, this.differentiator);
                    }

                    else { //ball keeps rolling
                        integrator.execute(current, this.timeStep, ax_s_gr, ay_s_gr, this.terrain, this.differentiator);
                    }
                }
            }

            else { // ball is moving
                integrator.execute(current, this.timeStep, ax_k_gr, ay_k_gr, this.terrain, this.differentiator);
            }

            //Don't allow to go over max speed
            current.set(3, Math.min(current.get(3), this.maxspeed));
            current.set(4, Math.min(current.get(4), this.maxspeed));

            //Add height only for testing, remove later
            current.add(this.terrain.setVariable("x", current.get(1)).setVariable("y", current.get(2)).evaluate());

            //Collisions? Save for later
            //Think about angle of bounce and conservation of momentum

            stateVectors.add(current);
        }

        this.initialState = new GVec4(this.timeStep);
        this.initialState.add(stateVectors.get(stateVectors.size()-1).get(1));
        this.initialState.add(stateVectors.get(stateVectors.size()-1).get(2));
        //Store the last position which is going to be the next starting position
        //Allow the user to choose the next starting velocities
    }
}
