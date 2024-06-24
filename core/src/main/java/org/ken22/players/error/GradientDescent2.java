package org.ken22.players.error;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.PhysicsFactory;
import org.ken22.physics.engine.PhysicsEngine;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.utils.MathUtils;

public class GradientDescent2 implements ErrorFunction {

    private GolfCourse course;
    private PhysicsFactory physicsFactory;

    public void init(GolfCourse course, PhysicsFactory physicsFactory) {
        this.course = course;
        this.physicsFactory = physicsFactory;
    }

    public double calculateError(StateVector4 state) {

        var engine = new PhysicsEngine(this.course, state);
        StateVector4 closest = state;
        double d_min = Double.POSITIVE_INFINITY;
        double x_t = course.targetXcoord();
        double y_t = course.targetYcoord();
        String terrain = course.courseProfile();
        Expression expr = course.expression;
        double z_t = expr.setVariable("x", x_t)
            .setVariable("y", y_t)
            .evaluate();

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

//        double d_total = MathUtils.magnitude(x_f-x_i, y_f-y_i, z_f-z_i);
        double vscale = v_i/5.0;
        double a = d_i-course.targetRadius();

//        double efficiency = (d_i-d_f)/d_total;
//        double action = (vscale)/(d_total);
        double improvement = vscale*d_f/(a);
        double overshoot = (v_f)/(Math.exp(d_f-course.targetRadius())*PhysicsEngine.MAX_SCORE_SPEED)-1;
        double feasibility = vscale*d_c/(a);

        return improvement+overshoot+feasibility;
    }

    public void init(GolfCourse course) {
        this.course = course;
    }
}
