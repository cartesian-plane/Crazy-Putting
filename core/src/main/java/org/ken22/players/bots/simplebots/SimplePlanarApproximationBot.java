package org.ken22.players.bots.simplebots;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.Player;
import org.ken22.utils.GolfExpression;

public class SimplePlanarApproximationBot implements Player {

    StateVector4 initState;
    StateVector4 targetState;

    GolfCourse course;
    Expression expr;

    public SimplePlanarApproximationBot(GolfCourse course) {
        this.course = course;
        this.expr = GolfExpression.expr(course);
    }

    @Override
    public StateVector4 play(StateVector4 state) {

        // init
        this.initState = state;
        this.targetState = new StateVector4(course.targetXcoord(), course.targetYcoord(), 0, 0);

        // solve
        var currentHeight = expr.setVariable("x", initState.x()).setVariable("y", initState.y()).evaluate();
        var targetHeight = expr.setVariable("x", targetState.x()).setVariable("y", targetState.y()).evaluate();

        var dx = targetState.x() - initState.x();
        var dy = targetState.y() - initState.y();
        var dz = targetHeight - currentHeight;

        //v0^2 = v1^2 + 2*a*d
        //a = -g*sin(alpha)
        var sinA = dz / Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        var cosA = Math.sqrt(1 - Math.pow(sinA, 2));

        var dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
        var v0p = Math.sqrt(2 * course.kineticFrictionGrass()*course.gravitationalConstant()*cosA * dist);
        var v0 = v0p * cosA;

        //vx/vy = dx/dy; vx^2 + vy^2 = v0^2
        //vx = vy * dx/dy; v0^2 = vx^2 + (vx * dx/dy)^2; v0^2 = (1 + (dx/dy)^2) * vx^2; vx = v0 / sqrt(1 + (dx/dy)^2)
        var vx = v0 / Math.sqrt(1 + Math.pow(dx/dy, 2));
        var vy = vx * dx/dy;

        return new StateVector4(initState.x(), initState.y(), vx, vy);
    }

}
