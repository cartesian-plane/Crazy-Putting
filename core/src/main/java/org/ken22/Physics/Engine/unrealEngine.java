package org.ken22.Physics.Engine;

import net.objecthunter.exp4j.Expression;
import org.ken22.Physics.Numerical_Derivation.NumDerivationMethod;
import org.ken22.Physics.Numerical_Integration.NumIntegrationMethod;
import org.ken22.Physics.System.PhysicsSystem;
import org.ken22.Physics.Vectors.GVec4;
import org.ken22.Physics.mathTools.myMath;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.interfaces.IFunc;
import java.util.ArrayList;
import java.util.Map;

import static org.ken22.Physics.Vectors.GVec4.copy;

public class unrealEngine {
    // Parameter order (t,x,y,vx, vy, gradx, grady, height)


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

    //Course specific information
    private Expression terrain; //Parameters are (x,y), passed in constructor
    private Map<String, Double> vars;
    private GolfCourse courseInfo;
    private String name;
    private double kf_gr;
    private double sf_gr;
    private double g;
    private double sf_sa;
    private double kf_sa;
    private double maxspeed;
    private double range;
    //Math
    private double timeStep;
    private NumIntegrationMethod integrator;
    private NumDerivationMethod differentiator;
    private GVec4 initialState; // (t,x,y,vx, vy)
    private ArrayList<GVec4> stateVectors = new ArrayList<GVec4>(); // (t,x,y,vx, vy, gradX, gradY)

    //Game and graphics

    int seconds = 0;
    public unrealEngine(PhysicsSystem system, NumIntegrationMethod integrator, NumDerivationMethod differentiator) {
        //Initial conditions
        this.initialState = system.getInitialState();
        this.stateVectors.add(initialState);

        //Math information
        this.timeStep = system.getTimeStep();
        this.integrator = integrator;
        this.differentiator = differentiator;
        this.vars = system.getMap();

        //Course info
        this.courseInfo = system.getCourse();
        kf_gr = this.courseInfo.getKineticFrictionGrass();
        sf_gr = this.courseInfo.getStaticFrictionGrass();
        g = this.courseInfo.getGravitationalConstant();
        sf_sa = this.courseInfo.getKineticFrictionSand();
        kf_sa = this.courseInfo.getStaticFrictionSand();
        this.terrain = system.getTerrain();
        this.maxspeed = courseInfo.getMaximumSpeed();
        this.range = courseInfo.getTerrainBounds();

//      System.out.println("kf_grass: " + kf_gr + ", sf_grass: " + sf_gr + ", g: " + g + ", sf_sand: " + sf_sa + ", kf_sand: " + kf_sa + ", maxspeed: " + maxspeed);
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
        boolean atRest = false;

        //Do not allow initial velocity above threshold
        currentState.set(3, Math.min(currentState.get(3), this.maxspeed));
        currentState.set(4, Math.min(currentState.get(4), this.maxspeed));

        while(!atRest) { //stop when target is reached
            System.out.println(currentState.getVector().toString() + " ------ ");

            assert (currentState.size() == 5);
            //calculates partial derivatives and adds them to currentState vector
            differentiator.gradients(currentState, this.terrain, this.range); // (t,x,y,vx, vy, gradX, gradY)

            //Check if on grass or sand

            //GVec4 stateVector, double timeStep, IFunc<Double, Double> funcx, IFunc<Double, Double> funcy, Expression height, NumDerivationMethod differentiator
            if(currentState.get(3) < Math.abs(0.05) && currentState.get(4) < Math.abs(0.05)) { //speed gets too low, 0.05 is threshold
                if(currentState.get(5) < Math.abs(0.05) && currentState.get(6) < Math.abs(0.05)) { //flat surface
                    atRest = true;
                    integrator.execute(this.stateVectors, this.timeStep, ax_s_gr, ay_s_gr, this.terrain, this.differentiator);
                }
                else { //inclined surface
                    if(myMath.pythagoras(currentState.get(5), currentState.get(6)) < sf_gr) { //ball stops
                        atRest = true;
                        integrator.execute(this.stateVectors, this.timeStep, ax_s_gr, ay_s_gr, this.terrain, this.differentiator);
                    }
                    else { //ball keeps rolling
                        integrator.execute(this.stateVectors, this.timeStep, ax_s_gr, ay_s_gr, this.terrain, this.differentiator);
                    }
                }
            }

            else { // ball is moving
                integrator.execute(stateVectors, this.timeStep, ax_k_gr, ay_k_gr, this.terrain, this.differentiator);
            }

            //Don't allow to go over max speed
            currentState.set(3, Math.min(currentState.get(3), this.maxspeed));
            currentState.set(4, Math.min(currentState.get(4), this.maxspeed));

            //Add height only for testing, remove later
            currentState.add(this.terrain.setVariable("x", currentState.get(1)).setVariable("y", currentState.get(2)).evaluate());

            //Collisions? Save for later
            //Think about angle of bounce and conservation of momentum

            stateVectors.add(GVec4.copy(currentState));
            currentState.skim();
            System.out.println(currentState.getVector().toString()+ " ------ ");
        }

        this.initialState = new GVec4(this.timeStep);
        this.initialState.add(stateVectors.get(stateVectors.size()-1).get(1));
        this.initialState.add(stateVectors.get(stateVectors.size()-1).get(2));
        this.stateVectors = null;

        //Store the last position which is going to be the next starting position
        //Remove completed trajectory from memory
        //Allow the user to choose the next starting velocities
    }

    public boolean nextFrame() {

        GVec4 currentState = this.stateVectors.getLast(); // (t,x,y,vx,vy)

        boolean atRest = false;
        int frames = 0;
        this.seconds++;

        //Do not allow initial velocity above threshold
        currentState.set(3, Math.min(currentState.get(3), this.maxspeed));
        currentState.set(4, Math.min(currentState.get(4), this.maxspeed));

        while(!atRest || frames >= 60) { //stop when target is reached
//          System.out.println(currentState.getVector().toString()+ " ------ ");

            frames++;
            assert (currentState.size() == 5);

            //Calculates partial derivatives and adds them to currentState vector (in place)
            differentiator.gradients(currentState, this.terrain, this.timeStep); // (t,x,y,vx, vy, gradX, gradY)

            //Check if on grass or sand

            GVec4 newState;

            //GVec4 stateVector, double timeStep, IFunc<Double, Double> funcx, IFunc<Double, Double> funcy, Expression height, NumDerivationMethod differentiator
            if(currentState.get(3) < Math.abs(0.05) && currentState.get(4) < Math.abs(0.05)) { //speed gets too low, 0.05 is threshold
                if(currentState.get(5) < Math.abs(0.05) && currentState.get(6) < Math.abs(0.05)) { //flat surface
                    atRest = true;
                    //Creates new stateVector
                    newState = integrator.execute(stateVectors, this.timeStep, ax_s_gr, ay_s_gr, this.terrain, this.differentiator);
                }
                else { //inclined surface
                    if(myMath.pythagoras(currentState.get(5), currentState.get(6)) < sf_gr) { //ball stops
                        atRest = true;
                        newState = integrator.execute(stateVectors, this.timeStep, ax_s_gr, ay_s_gr, this.terrain, this.differentiator);
                    }
                    else { //ball keeps rolling
                        newState = integrator.execute(stateVectors, this.timeStep, ax_s_gr, ay_s_gr, this.terrain, this.differentiator);
                    }
                }
            }

            else { // ball is moving
                newState = integrator.execute(stateVectors, this.timeStep, ax_k_gr, ay_k_gr, this.terrain, this.differentiator);
            }

            //Don't allow to go over max speed
            newState.set(3, Math.min(newState.get(3), this.maxspeed));
            newState.set(4, Math.min(newState.get(4), this.maxspeed));

            //Calculate height
            newState.add(this.terrain.setVariable("x", newState.get(1)).setVariable("y", newState.get(2)).evaluate());

            //Check if in water or out of bounds
            atRest = ((Math.abs(newState.get(1)) > this.range || Math.abs(newState.get(2)) > this.range) || (newState.getLast() < 0));

            stateVectors.add(newState);
            currentState = newState;
            //          System.out.println(currentState.getVector().toString()+ " ------ ");
        }

        //Use a queue to store the statevectors so that we only store 30 frames at any given time
        //Instead of deleting every 5 seconds
        if (this.seconds == 5) {
            GVec4 last = copy(stateVectors.getLast());
            this.initialState = last;
            this.stateVectors = new ArrayList<GVec4>();
            this.stateVectors.add(initialState);
        }

        else if (atRest) {
            this.initialState = new GVec4(this.timeStep);
            this.initialState.add(stateVectors.get(stateVectors.size()-1).get(1));
            this.initialState.add(stateVectors.get(stateVectors.size()-1).get(2));
            //Store the last position which is going to be the next starting position
            //Allow the user to choose the next starting velocities
        }

        return atRest;
    }
}
