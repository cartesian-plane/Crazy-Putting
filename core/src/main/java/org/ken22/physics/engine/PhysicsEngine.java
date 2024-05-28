package org.ken22.physics.engine;

import net.objecthunter.exp4j.Expression;
import org.ken22.physics.numerical_derivation.NumDerivationMethod;
import org.ken22.physics.numerical_integration.NumIntegrationMethod;
import org.ken22.physics.system.PhysicsSystem;
import org.ken22.physics.vectors.GVec4;
import org.ken22.physics.mathTools.MyMath;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.interfaces.IFunc;
import java.util.ArrayList;
import java.util.Map;

import static org.ken22.physics.vectors.GVec4.copy;

public class PhysicsEngine {
    // Parameter order (t,x,y,vx, vy, gradx, grady, height)

    //Grass equations

    //Kinetic
    private IFunc<Double, Double> ax_k_gr = (vars) ->
        ( -1*this.g*(vars.get(5)+this.kf_gr*vars.get(3) /
            MyMath.pythagoras(vars.get(3), vars.get(4)) ));
    private IFunc<Double, Double> ay_k_gr = (vars) ->
        ( -1*this.g*(vars.get(6)+this.kf_gr*vars.get(4) /
            MyMath.pythagoras(vars.get(3), vars.get(4)) ));
    //Static
    private IFunc<Double, Double> ax_s_gr = (vars) ->
        ( -1*this.g*(vars.get(5)+this.kf_gr*vars.get(5) /
            MyMath.pythagoras(vars.get(5), vars.get(6)) ));
    private IFunc<Double, Double> ay_s_gr = (vars) ->
        ( -1*this.g*(vars.get(6)+this.kf_gr*vars.get(6) /
            MyMath.pythagoras(vars.get(5), vars.get(6)) ));


    //Sand equations

    //Kinetic
    private IFunc<Double, Double> ax_k_sa = (vars) ->
        ( -1*this.g*(vars.get(5)+this.kf_sa*vars.get(3) /
            MyMath.pythagoras(vars.get(3), vars.get(4)) ));
    private IFunc<Double, Double> ay_k_sa = (vars) ->
        ( -1*this.g*(vars.get(6)+this.kf_sa*vars.get(4) /
            MyMath.pythagoras(vars.get(3), vars.get(4)) ));
    //Static
    private IFunc<Double, Double> ax_s_sa = (vars) ->
        ( -1*this.g*(vars.get(5)+this.sf_sa*vars.get(5) /
            MyMath.pythagoras(vars.get(5), vars.get(6)) ));
    private IFunc<Double, Double> ay_s_sa = (vars) ->
        ( -1*this.g*(vars.get(6)+this.sf_sa*vars.get(6) /
            MyMath.pythagoras(vars.get(5), vars.get(6)) ));

    //Json
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
    private double targetX;
    private double targetY;
    private double targetRadius;

    //Math
    private double timeStep;
    private NumIntegrationMethod integrator;
    private NumDerivationMethod differentiator;
    private GVec4 initialState; // (t,x,y,vx, vy)
    private ArrayList<GVec4> stateVectors = new ArrayList<GVec4>(); // (t,x,y,vx, vy, gradX, gradY)

    //Game and graphics
    double seconds = 0.0;
    boolean atRest = false;
    public PhysicsEngine(PhysicsSystem system, NumIntegrationMethod integrator, NumDerivationMethod differentiator) {
        //Initial conditions
        this.initialState = system.getInitialState();
        this.stateVectors.add(initialState);
        this.initialState.add(0.0); //initiialising values for gradX
        this.initialState.add(0.0); //initialising values for gradY since they are edited in place
        this.initialState.add(0.0); //initialising values for height since they are edited in place

        //Math information
        this.timeStep = system.getTimeStep();
        this.integrator = integrator;
        this.differentiator = differentiator;
        this.vars = system.getMap();

        //Course info
        this.courseInfo = system.getCourse();
        this.kf_gr = this.courseInfo.kineticFrictionGrass();
        this.sf_gr = this.courseInfo.staticFrictionGrass();
        this.g = this.courseInfo.gravitationalConstant();
        this.sf_sa = this.courseInfo.kineticFrictionSand();
        this.kf_sa = this.courseInfo.staticFrictionSand();
        this.terrain = system.getTerrain();
        this.maxspeed = courseInfo.maximumSpeed();
        this.range = courseInfo.range();
        this.targetX = courseInfo.targetXcoord();
        this.targetY = courseInfo.targetYcoord();

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

    public void run()
    {
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
            if(minSpeedReached(currentState)) { //speed gets too low, 0.05 is threshold
                if(currentState.get_dhdx() < Math.abs(0.05) && currentState.get_dhdy() < Math.abs(0.05)) { //flat surface
                    atRest = true;
                    integrator.execute(this.stateVectors, this.timeStep, ax_s_gr, ay_s_gr, this.terrain, this.differentiator);
                }
                else { //inclined surface
                    if(MyMath.pythagoras(currentState.get_dhdx(), currentState.get_dhdy()) < sf_gr) { //ball stops
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
            currentState.set(3, Math.min(currentState.get_vx(), this.maxspeed));
            currentState.set(4, Math.min(currentState.get_vy(), this.maxspeed));

            //Add height only for testing, remove later
            currentState.add(this.terrain.setVariable("x", currentState.get_x()).setVariable("y", currentState.get_y()).evaluate());

            //Collisions? Save for later
            //Think about angle of bounce and conservation of momentum

            stateVectors.add(GVec4.copy(currentState));
            currentState.skim();
            System.out.println(currentState.getVector().toString()+ " ------ ");
        }

        this.initialState = new GVec4(this.timeStep);
        this.initialState.add(stateVectors.getLast().get_x());
        this.initialState.add(stateVectors.getLast().get_y());
        this.stateVectors = null;

        //Store the last position which is going to be the next starting position
        //Remove completed trajectory from memory
        //Allow the user to choose the next starting velocities
    }

    public GVec4 nextStep() {

        GVec4 currentState = this.stateVectors.getLast(); // (t,x,y,vx,vy, gradX, gradY)
        this.seconds += 1.0/60.0;

        //Do not allow initial velocity above threshold
        currentState.set(3, capVelocity(currentState.get_vx()));
        currentState.set(4, capVelocity(currentState.get_vy()));

        //System.out.println(currentState.getVector().toString()+ " ------ ");

        //Calculates partial derivatives and adds them to currentState vector (in place)
        differentiator.gradients(currentState, this.terrain, this.timeStep/10.0); // (t,x,y,vx, vy, gradX, gradY)
        currentState.set(7, terrain.setVariable("x", currentState.get_x()).setVariable("y", currentState.get_y()).evaluate());
        ArrayList<IFunc<Double, Double>> functions = chooseFunctions(currentState);
        GVec4 newState = integrator.execute(stateVectors, this.timeStep, functions.getFirst(), functions.get(1), this.terrain, this.differentiator);

        newState.set(3, capVelocity(newState.get_vx()));
        newState.set(4, capVelocity(newState.get_vy()));

        stateVectors.add(newState);
        //System.out.println(currentState.getVector().toString()+ " ------ ");

        /* if (this.seconds == 5) {
            this.initialState = copy(stateVectors.getLast());
            this.stateVectors = new ArrayList<GVec4>();
            this.stateVectors.add(initialState);
        }*/

        return stateVectors.getLast();
    }

    public boolean isAtRest(GVec4 currentState) {
        boolean atRest = false;
        double vx = Math.abs(currentState.get_vx());
        double vy = Math.abs(currentState.get_vy());
        double gradX = Math.abs(currentState.get_dhdx());
        double gradY = Math.abs(currentState.get_dhdy());

        if(vx < 0.01 && vy < 0.01) { // speed threshold
            if(gradX < 0.001 && gradY < 0.001) { // flat surface
                atRest = true;
            } else if(MyMath.pythagoras(gradX, gradY) < sf_gr) { // inclined surface but within static friction
                atRest = true;
            }
        }

        // Check if in water or out of bounds
        if(!atRest) {
            double x = Math.abs(currentState.get_x());
            double y = Math.abs(currentState.get_y());
            atRest = (x > this.range || y > this.range || (currentState.getLast() < 0));
            if(atRest) {
                currentState.set(3, 0.0); // Zero velocities
                currentState.set(4, 0.0);
            }
        }

        // Check if target reached
        if(!atRest) {
            double x = Math.abs(currentState.get_x() - this.targetX);
            double y = Math.abs(currentState.get_y() - this.targetY);
            atRest = (x < this.targetRadius && y < this.targetRadius);
            if(atRest) {
                System.out.println("Score!");
            }
        }
        return atRest;
    }

    private ArrayList<IFunc<Double, Double>> chooseFunctions(GVec4 currentState) {
        ArrayList<IFunc<Double, Double>> functions = new ArrayList<IFunc<Double, Double>>();
        if(minSpeedReached(currentState)) { //speed gets too low, 0.05 is threshold
            functions.add(this.ax_s_gr);
            functions.add(this.ay_s_gr);
        }

        else { // ball is moving
            functions.add(this.ax_k_gr);
            functions.add(this.ay_k_gr);
        }
        return functions;
    }

    public void restart(double vx, double vy) {
        this.initialState = new GVec4(this.timeStep);
        this.initialState.add(stateVectors.getLast().get_x());
        this.initialState.add(stateVectors.getLast().get_y());
        this.initialState.add(vx);
        this.initialState.add(vy);
        this.stateVectors = new ArrayList<GVec4>();
        this.stateVectors.add(initialState);
        //Store the last position which is going to be the next starting position
        //Allow the user to choose the next starting velocities
    }

    private boolean minSpeedReached(GVec4 currentState) {
        return (Math.abs(currentState.get_vx()) < 0.01 && Math.abs(currentState.get_vy()) < 0.01);
    }

    private double capVelocity(double velocity) {

        if(velocity > this.maxspeed) {
            velocity = this.maxspeed;
        }
        if(velocity < -this.maxspeed) {
            velocity = -this.maxspeed;
        }
        return velocity;
    }
}
