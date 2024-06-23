package org.ken22.players.error;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.engine.PhysicsEngine;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.utils.MathUtils;

public class GradientDescent1 implements ErrorFunction {

    private GolfCourse course;

    public double calculateError(StateVector4 state) {
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
    }

    public void init(GolfCourse course) {
        this.course = course;
    }
}
