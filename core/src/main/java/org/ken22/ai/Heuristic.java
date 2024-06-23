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

    //Bad heuristic for many reasons
    HEURISTIC1((state, course) -> {

        var engine = new PhysicsEngine(course, state);
        StateVector4 closest = state;
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

        System.out.println("Target coordinates: (" + x_t + ", " + y_t + ", " + z_t + ")");

        while (!engine.isAtRest()) {
            StateVector4 vec = engine.nextStep();
            double x = vec.x();
            double y = vec.y();
            double z = expr.setVariable("x", x)
                .setVariable("y", y)
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

        System.out.println("Initial state: (" + x_i + ", " + y_i + ", " + z_i + ")");

        double x_c = closest.x();
        double y_c = closest.y();

        double x_f = last.x();
        double y_f = last.y();
        double vx_f = last.vx();
        double vy_f = last.vy();
        double z_f = expr.setVariable("x", x_f).setVariable("y", y_f).evaluate();

        System.out.println("Final state: (" + x_f + ", " + y_f + ", " + z_f + ")");
        System.out.println("Final velocities: (" + vx_f + ", " + vy_f + ")");

        double x_feasible = Math.abs(((x_t-x_c)/(x_t-x_i))/(5.0-vx_i));
        double x_overshoot = Math.abs(((Math.abs(vx_f))/(x_f-x_t+PhysicsEngine.MAX_SCORE_SPEED)));
        double x_improvement = Math.abs((x_t-x_f)/(x_t-x_i));
        double x_goodness = x_feasible+x_overshoot+x_improvement;

        System.out.println("x_feasible: " + x_feasible);
        System.out.println("x_overshoot: " + x_overshoot);
        System.out.println("x_improvement: " + x_improvement);
        System.out.println("x_goodness: " + x_goodness);

        double y_feasible = Math.abs(((y_t-y_c)/(y_t-y_i))/(5.0-vy_i));
        double y_overshoot = Math.abs(((Math.abs(vy_f))/(y_f-y_t+PhysicsEngine.MAX_SCORE_SPEED)));
        double y_improvement = Math.abs((y_t-y_f)/(y_t-y_i));
        double y_goodness = y_feasible+y_overshoot+y_improvement;

        System.out.println("y_feasible: " + y_feasible);
        System.out.println("y_overshoot: " + y_overshoot);
        System.out.println("y_improvement: " + y_improvement);
        System.out.println("y_goodness: " + y_goodness);

//        double z_improvement = Math.abs((z_t-z_f)/(z_t-z_i));
//        System.out.println("z_improvement: " + z_improvement);

        double d_i = MathUtils.magnitude(x_i-x_t, y_i-y_t, z_i-z_t);
        double d_f = MathUtils.magnitude(x_f-x_t, y_f-y_t, z_f-z_t);
        double usefulness = (d_f/d_i)/(MathUtils.magnitude(vx_i, vy_i, 0.0)/5.0);

        System.out.println("d_i: " + d_i);
        System.out.println("d_f: " + d_f);
        System.out.println("usefulness: " + usefulness);

        return x_goodness+y_goodness/*+z_improvement*/+usefulness;
    }),

    //Better heuristic compared to Heuristic 1
    HEUR2((state, course) -> {

        var engine = new PhysicsEngine(course, state);
        StateVector4 closest = state;
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

        System.out.println("Target coordinates: (" + x_t + ", " + y_t + ", " + z_t + ")");

        while (!engine.isAtRest()) {
            StateVector4 vec = engine.nextStep();
            double x = vec.x();
            double y = vec.y();
            double z = expr.setVariable("x", x)
                .setVariable("y", y)
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
        double d_i = MathUtils.magnitude(x_t-x_i, y_t-y_i, z_t-z_i);
        double v_i = MathUtils.magnitude(vx_i, vy_i);

        System.out.println("Initial state: (" + x_i + ", " + y_i + ", " + z_i + ")");

        double x_c = closest.x();
        double y_c = closest.y();
        double z_c = expr.setVariable("x", x_c).setVariable("y", y_c).evaluate();
        double d_c = MathUtils.magnitude(x_t-x_c, y_t-y_c, z_t-z_c);

        double x_f = last.x();
        double y_f = last.y();
        double vx_f = last.vx();
        double vy_f = last.vy();
        double z_f = expr.setVariable("x", x_f).setVariable("y", y_f).evaluate();
        double d_f = MathUtils.magnitude(x_f-x_t, y_f-y_t, z_f-z_t);
        double v_f = MathUtils.magnitude(vx_f, vy_f);

        System.out.println("Final state: (" + x_f + ", " + y_f + ", " + z_f + ")");
        System.out.println("Final velocities: (" + vx_f + ", " + vy_f + ")");

        double d_total = MathUtils.magnitude(x_f-x_i, y_f-y_i, z_f-z_i);
        double vscale = v_i/5;
        double a = d_i-course.targetRadius();

//        double efficiency = (d_i-d_f)/d_total;
//        double action = (vscale)/(d_total);
        double improvement = vscale*d_f/(a);
        double overshoot = (v_f)/(Math.exp(d_f-course.targetRadius())*PhysicsEngine.MAX_SCORE_SPEED)-1;
        double feasibility = vscale*d_c/(a);

        return improvement+overshoot+feasibility;
    });

    private final BiFunction<StateVector4, GolfCourse, Double> function;

    Heuristic(BiFunction<StateVector4, GolfCourse, Double> function) {
        this.function = function;
    }

    public double apply(StateVector4 vector, GolfCourse course) {
        return function.apply(vector, course);
    }

}
