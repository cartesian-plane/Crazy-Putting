package org.ken22.players.bots;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.odesolvers.ODESolver;
import org.ken22.physics.engine.PhysicsEngine;
import org.ken22.physics.differentiators.Differentiator;
import org.ken22.physics.utils.PhysicsUtils;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.Input2;
import org.ken22.players.Player;
import org.ken22.players.error.ErrorFunction;

import java.util.function.Function;

/**
 * A bot that uses hill climbing on the input space of initial velocities to find the best shot.
 */
public class HillClimbingBot implements Player {
    private static final double CONVERGENCE_THRESHOLD = 0.1;
    private boolean converged = false;

    StateVector4 currentState;
    StateVector4 targetState;

    ErrorFunction errorFunction;
    Differentiator differentiator;
    ODESolver<StateVector4> solver;

    GolfCourse course;
    PhysicsEngine engine;
    private Function<Double, Double> vyError;
    private Function<Double, Double> vxError;
    Input2 currentBest;
    double stepSize; // Default step size

    public HillClimbingBot(ErrorFunction errorFunction, Differentiator dif, ODESolver<StateVector4> solver, double stepSize) {
        this.errorFunction = errorFunction;
        this.stepSize = stepSize;
        this.differentiator = dif;
        this.solver = solver;
    }

    @Override
    public StateVector4 play(StateVector4 state) {
        init(state, course);

        do {
            climb();
            var curSol =  new StateVector4(currentState.x(), currentState.y(), currentBest.vx(), currentBest.vy());
            System.out.println("Current solution: " + curSol);
            var end = predict(currentState, currentBest.vx(), currentBest.vy(), course);
            System.out.println("End state: " + end + "; Error: " + errorFunction.calculateError(end));
        } while (!converged);

        StateVector4 fin =  predict(currentState, currentBest.vx(), currentBest.vy(), course);
        System.out.println(errorFunction.calculateError(fin));
        System.out.println("Final sstate: " + fin);

        return new StateVector4(currentState.x(), currentState.y(), currentBest.vx(), currentBest.vy());
    }

    private void climb() {
        double vxErrorSlope = differentiator.differentiate(stepSize, currentBest.vx(), vxError);
        double vyErrorSlope = differentiator.differentiate(stepSize, currentBest.vy(), vyError);
        System.out.println("Vx error slope: " + vxErrorSlope + "; Vy error slope: " + vyErrorSlope);

        converged = PhysicsUtils.magnitude(vxErrorSlope, vyErrorSlope) < CONVERGENCE_THRESHOLD;

        //temporarily not in terms of stepsize bc it is too slow
        var c =  new Input2(currentBest.vx() - stepSize*vxErrorSlope, currentBest.vy() - stepSize*vyErrorSlope);
        currentBest = c;
    }

    private StateVector4 predict(StateVector4 state, double vx, double vy, GolfCourse course) {
        PhysicsEngine engine = new PhysicsEngine(course, new StateVector4(state.x(), state.y(), vx, vy), stepSize, differentiator, solver);
        return engine.solve();
    }

    private void  init(StateVector4 state, GolfCourse course) {
        this.currentState = state;
        this.course = course;
        this.errorFunction.init(course);

        this.currentBest = new Input2(currentState.vx(), currentState.vy());
        this.targetState = new StateVector4(course.targetXcoord(), course.targetYcoord(), 0, 0);
        //System.out.println("Target state: " + targetState);

        this.vxError = (vx) -> {
            // Create a new state vector with the inputed x velocity and current best y velocity
            var probingState = new StateVector4(currentState.x(), currentState.y(), vx, currentBest.vy());
            // Run a simulation to determine the error of the current best
            engine = new PhysicsEngine(course, probingState, stepSize, differentiator, solver);
            StateVector4 finalState = engine.solve();

            //System.out.println("Final state: " + finalState);

            return errorFunction.calculateError(finalState);
        };

        this.vyError = (vy) -> {
            // Create a new state vector with the inputed y velocity and current best x velocity
            var probingState = new StateVector4(currentState.x(), currentState.y(), currentBest.vx(), vy);
            // Run a simulation to determine the error of the current best
            engine = new PhysicsEngine(course, probingState, stepSize, differentiator, solver);
            StateVector4 finalState = engine.solve();

            //System.out.println("Final state: " + finalState);

            return errorFunction.calculateError(finalState);
        };
    }
}
