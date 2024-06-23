package org.ken22.ai;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.engine.PhysicsEngine;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.utils.MathUtils;

import java.util.function.BiFunction;

@Deprecated
public enum Heuristic {

    //TODO remove this
    EUCLIDIAN2D((state, course) -> {
        double targetX = course.targetXcoord();
        double targetY = course.targetYcoord();
        var engine = new PhysicsEngine(course, state);
        while (!engine.isAtRest()) {
            engine.nextStep();
        }
        StateVector4 lastVector = engine.getTrajectory().getLast();
        // get the position where the ball stopped
        double finalX = lastVector.x();
        double finalY = lastVector.y();
        // compute the cost function h
        return MathUtils.magnitude(finalX - targetX, finalY - targetY);
    }),

    EUCLIDIAN3D((state, course) -> {
        double targetX = course.targetXcoord();
        double targetY = course.targetYcoord();
        var engine = new PhysicsEngine(course, state);

        while (!engine.isAtRest()) {
            engine.nextStep();
        }

        StateVector4 lastVector = engine.getTrajectory().getLast();
        // get the position where the ball stopped
        double finalX = lastVector.x();
        double finalY = lastVector.y();

        String terrain = course.courseProfile();
        Expression expr = new ExpressionBuilder(terrain)
            .variables("x", "y")
            .build();
        double targetHeight = expr.setVariable("x", targetX)
            .setVariable("y", targetY)
            .evaluate();
        double finalHeight = expr.setVariable("x", finalX)
            .setVariable("y", finalY)
            .evaluate();

        // compute the cost function h
        return MathUtils.magnitude(finalX - targetX, finalY - targetY, finalHeight - targetHeight);
    }),

    HEURISTIC1((state, course) -> {

        var engine = new PhysicsEngine(course, state);
        StateVector4 closest = null;
        double d_min = Double.POSITIVE_INFINITY;
        double x_t = course.targetXcoord();
        double y_t = course.targetYcoord();
        String terrain = course.courseProfile();
        Expression expr = new ExpressionBuilder(terrain)
            .variables("x", "y")
            .build();
        double z_t = expr.setVariable("x", x_t)
            .setVariable("y", y_t)
            .evaluate();

        while (!engine.isAtRest()) {
            engine.nextStep();
            StateVector4 vec = engine.getTrajectory().getLast();
            double x = vec.x();
            double y = vec.y();
            double z = expr.setVariable("x", x_t)
                .setVariable("y", y_t)
                .evaluate();
            double current = MathUtils.magnitude(x - x_t, y - y_t, z - z_t);
            if(current < d_min) {
                d_min = current;
                closest = vec;
            }
        }

        StateVector4 last = engine.getTrajectory().getLast();
        StateVector4 initial = engine.getTrajectory().getFirst();

        double x_i = initial.x();
        double y_i = initial.y();
        double vx_i = initial.vx();
        double vy_i = initial.vy();
        double z_i = expr.setVariable("x", x_i).setVariable("y", y_i).evaluate();

        double x_c = closest.x();
        double y_c = closest.y();
//        double vx_c = closest.vx();
//        double vy_c = closest.vy();
//        double z_c = expr.setVariable("x", x_c).setVariable("y", y_c).evaluate();

        double x_f = last.x();
        double y_f = last.y();
        double vx_f = last.vx();
        double vy_f = last.vy();
        double z_f = expr.setVariable("x", x_f).setVariable("y", y_f).evaluate();


        // There are three heuristics
        // Feasibility: can the ball reach the target in one shot?
        // Overshoot: is the speed at the target low enough?
        // Improvement: does the ball get closer to the target?

        //x-direction goodness
        double x_feasible = Math.abs(((x_t-x_c)/(x_t-x_i))/(5-vx_i));
        double x_overshoot = Math.abs(((Math.abs(vx_f))/(x_f-x_t+PhysicsEngine.MAX_SCORE_SPEED)));
        double x_improvement = Math.abs((x_t-x_f)/(x_t-x_i));
        double x_goodness = x_feasible+x_overshoot+x_improvement;

        //y-direction goodness
        double y_feasible = Math.abs(((y_t-y_c)/(y_t-y_i))/(5-vy_i));
        double y_overshoot = Math.abs(((Math.abs(vy_f))/(y_f-y_t+PhysicsEngine.MAX_SCORE_SPEED)));
        double y_improvement = Math.abs((y_t-y_f)/(y_t-y_i));
        double y_goodness = y_feasible+y_overshoot+y_improvement;

       //z-direction goodness
        double z_improvement = Math.abs((z_t-z_f)/(z_t-z_i));

        //general usefulness
        double d_i = MathUtils.magnitude(x_i-x_t, y_i-y_t, z_i-z_t);
        double d_f = MathUtils.magnitude(x_f-x_t, y_f-y_t, z_f-z_t);
        double usefuleness = (d_f/d_i)/(MathUtils.magnitude(vx_i, vy_i, 0)/5);

        return x_goodness+y_goodness+z_improvement+usefuleness;
    });

    private final BiFunction<StateVector4, GolfCourse, Double> function;

    Heuristic(BiFunction<StateVector4, GolfCourse, Double> function) {
        this.function = function;
    }

    public double apply(StateVector4 vector, GolfCourse course) {
        return function.apply(vector, course);
    }

}
