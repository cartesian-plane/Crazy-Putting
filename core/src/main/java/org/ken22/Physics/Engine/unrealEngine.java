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

import static org.ken22.Physics.Vectors.GVec4.copy;

public class unrealEngine {

    // parameter order (t,x,y,vx, vy, gradx, grady, height)


    //Grass equations

    //Kinetic
    private IFunc<Double, Double> ax_k_gr = (vars) ->
        ( -1*this.g*(vars.get(5)+this.kf_gr*vars.get(3) /
            myMath.pythagoras(vars.get(5), vars.get(6)) ));
    private IFunc<Double, Double> ay_k_gr = (vars) ->
        ( -1*this.g*(vars.get(6)+this.kf_gr*vars.get(4) /
            myMath.pythagoras(vars.get(5), vars.get(6)) ));

    //Static
    private IFunc<Double, Double> ax_s_gr = (vars) ->
        ( -1*this.g*(vars.get(5)+this.sf_gr*vars.get(5) /
            myMath.pythagoras(vars.get(5), vars.get(6)) ));
    private IFunc<Double, Double> ay_s_gr = (vars) ->
        ( -1*this.g*(vars.get(6)+this.sf_gr*vars.get(6) /
            myMath.pythagoras(vars.get(5), vars.get(6))));


    //Sand equations

    //Kinetic
    private IFunc<Double, Double> ax_k_sa = (vars) ->
        ( -1*this.g*(vars.get(5)+this.kf_sa*vars.get(3) /
            myMath.pythagoras(vars.get(3), vars.get(4)) ));
    private IFunc<Double, Double> ay_k_sa = (vars) ->
        ( -1*this.g*(vars.get(6)+this.kf_sa*vars.get(4) /
            myMath.pythagoras(vars.get(3), vars.get(4)) ));

    //Static
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
    private GolfCourse courseInfo;
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
        this.courseInfo = system.getCourse();
        this.integrator = integrator;
        this.differentiator = differentiator;
        this.terrain = system.getTerrain();
        this.maxspeed = system.getCourse().getMaximumSpeed();
        this.initialState = system.getInitialState();
        this.stateVectors.add(initialState);
        kf_gr = this.courseInfo.getKineticFrictionGrass();
        sf_gr = this.courseInfo.getStaticFrictionGrass();
        g = this.courseInfo.getGravitationalConstant();
        sf_sa = this.courseInfo.getKineticFrictionSand();
        kf_sa = this.courseInfo.getStaticFrictionSand();
        System.out.println("kf_grass: " + kf_gr + ", sf_grass: " + sf_gr + ", g: " + g + ", sf_sand: " + sf_sa + ", kf_sand: " + kf_sa + ", maxspeed: " + maxspeed);
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

        GVec4 currentState = copy(initialState); // (t,x,y,vx,vy)
        assert currentState.size() == 5;
        boolean atRest = false;

        //Do not allow initial velocity above threshold
        currentState.set(3, Math.min(currentState.get(3), this.maxspeed));
        currentState.set(4, Math.min(currentState.get(4), this.maxspeed));

        while(!atRest) { //stop when target is reached

            //calculates partial derivatives and adds them to currentState vector
            differentiator.gradients(currentState, this.terrain, this.timeStep); // (t,x,y,vx, vy, gradX, gradY)

            //Check if on grass or sand

            //GVec4 stateVector, double timeStep, IFunc<Double, Double> funcx, IFunc<Double, Double> funcy, Expression height, NumDerivationMethod differentiator
            if(currentState.get(3) < Math.abs(0.05) && currentState.get(4) < Math.abs(0.05)) { //speed gets too low, 0.05 is threshold
                if(currentState.get(5) < Math.abs(0.05) && currentState.get(6) < Math.abs(0.05)) { //flat surface
                    atRest = true;
                    integrator.execute(currentState, this.timeStep, ax_s_gr, ay_s_gr, this.terrain, this.differentiator);
                }

                else { //inclined surface
                    if(myMath.pythagoras(currentState.get(5), currentState.get(6)) < sf_gr) { //ball stops
                        atRest = true;
                        integrator.execute(currentState, this.timeStep, ax_s_gr, ay_s_gr, this.terrain, this.differentiator);
                    }

                    else { //ball keeps rolling
                        integrator.execute(currentState, this.timeStep, ax_s_gr, ay_s_gr, this.terrain, this.differentiator);
                    }
                }
            }

            else { // ball is moving
                integrator.execute(currentState, this.timeStep, ax_k_gr, ay_k_gr, this.terrain, this.differentiator);
            }

            //Don't allow to go over max speed
            currentState.set(3, Math.min(currentState.get(3), this.maxspeed));
            currentState.set(4, Math.min(currentState.get(4), this.maxspeed));

            //Add height only for testing, remove later
            currentState.add(this.terrain.setVariable("x", currentState.get(1)).setVariable("y", currentState.get(2)).evaluate());

            //Collisions? Save for later
            //Think about angle of bounce and conservation of momentum

            stateVectors.add(currentState);
            System.out.println(currentState.getVector().toString()+" ------ ");
        }

        this.initialState = new GVec4(this.timeStep);
        this.initialState.add(stateVectors.get(stateVectors.size()-1).get(1));
        this.initialState.add(stateVectors.get(stateVectors.size()-1).get(2));
        //Store the last position which is going to be the next starting position
        //Allow the user to choose the next starting velocities
    }
}
