package org.ken22.Physics.Engine;

import net.objecthunter.exp4j.Expression;
import org.ken22.Physics.Numerical_Derivation.NumDerivationMethod;
import org.ken22.Physics.Numerical_Derivation.basicDerivation;
import org.ken22.Physics.Numerical_Integration.NumIntegrationMethod;
import org.ken22.Physics.System.PhysicsSystem;
import org.ken22.Physics.Vectors.GVec4;
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
            (Math.sqrt( Math.pow(vars.get(3),2)+Math.pow(vars.get(4),2)))) );
    private IFunc<Double, Double> ay_k_gr = (vars) ->
        ( -1*this.g*(vars.get(6)+this.kf_gr*vars.get(4) /
            (Math.sqrt(Math.pow(vars.get(3),2)+Math.pow(vars.get(4),2)))));;
    private IFunc<Double, Double> ax_s_gr = (vars) ->
        ( -1*this.g*(vars.get(5)+this.sf_gr*vars.get(5) /
            (Math.sqrt( Math.pow(vars.get(5),2)+Math.pow(vars.get(6),2)))) );
    private IFunc<Double, Double> ay_s_gr = (vars) ->
        ( -1*this.g*(vars.get(6)+this.sf_gr*vars.get(6) /
            (Math.sqrt(Math.pow(vars.get(5),2)+Math.pow(vars.get(6),2)))));;
    private IFunc<Double, Double> ax_k_sa = (vars) ->
        ( -1*this.g*(vars.get(5)+this.kf_sa*vars.get(3) /
            (Math.sqrt( Math.pow(vars.get(3),2)+Math.pow(vars.get(4),2)))) );
    private IFunc<Double, Double> ay_k_sa = (vars) ->
        ( -1*this.g*(vars.get(6)+this.kf_sa*vars.get(4) /
            (Math.sqrt(Math.pow(vars.get(3),2)+Math.pow(vars.get(4),2)))));;
    private IFunc<Double, Double> ax_s_sa = (vars) ->
        ( -1*this.g*(vars.get(5)+this.sf_sa*vars.get(5) /
            (Math.sqrt( Math.pow(vars.get(5),2)+Math.pow(vars.get(6),2)))) );
    private IFunc<Double, Double> ay_s_sa = (vars) ->
        ( -1*this.g*(vars.get(6)+this.sf_sa*vars.get(6) /
            (Math.sqrt(Math.pow(vars.get(5),2)+Math.pow(vars.get(6),2)))));

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
    private ArrayList<ArrayList<Double>> stateVectors = new ArrayList<ArrayList<Double>>(); // (t,x,y,vx, vy, gradX, gradY)
    public unrealEngine(PhysicsSystem system, NumIntegrationMethod integrator, NumDerivationMethod differentiator) {
        this.vars = system.getMap();
        this.timeStep = system.getTimeStep();
        this.course = system.getCourse();
        this.integrator = integrator;
        this.differentiator = differentiator;
        this.terrain = system.getTerrain();
        this.maxspeed = system.getCourse().getMaximumSpeed();
    }

    public void solve() {

        GVec4 current = initialState;
        double t = current.get(0);
        boolean atRest = false;

        //do not allow velocity above maxspeed threshold
        current.set(3, Math.min(current.get(3), this.maxspeed));
        current.set(4, Math.min(current.get(4), this.maxspeed));

        basicDerivation derivator = new basicDerivation(current, this.terrain, this.timeStep);

        while(!atRest) {

            //Calculate slopes



            //Check if on grass or sand


            if(current.get(3) < 0.05 & current.get(4) < 0.05) { //speed gets too low
                if(current.get(5) < 0.01 & current.get(6) < 0.01) { //flat surface
                    atRest = true;
                    break; //Is it possible to simply break?
                }

                else { //inclined surface
                    //ball keeps rolling

                    //ball stops
                }
            }

            //Don't allow to go over max speed
            current.set(3, Math.min(current.get(3), this.maxspeed));
            current.set(4, Math.min(current.get(4), this.maxspeed));

            //Collisions? Save for later
            //Think about angle of bounce and conservation of momentum

            //continue normally
            t += this.timeStep;
        }

        //Store the last position which is going to be the next starting position
        //Allow the user to choose the next starting velocities
    }

    public void updateState(NumIntegrationMethod method, GVec4 current, double ax, double ay) {

    }

    public ArrayList<Double> gradient(NumDerivationMethod method, GVec4 current, double height, double timeStep) {
        return null;
    }

}
