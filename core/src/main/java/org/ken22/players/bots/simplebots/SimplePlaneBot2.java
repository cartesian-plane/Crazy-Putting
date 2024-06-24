package org.ken22.players.bots.simplebots;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.Player;
import org.ken22.utils.GolfExpression;

public class SimplePlaneBot2  implements Player {

    StateVector4 initState;
    StateVector4 targetState;

    GolfCourse course;
    Expression expr;

    public SimplePlaneBot2(GolfCourse course) {
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
        var dz_dx = dz/dx;
        var dz_dy = dz/dy;
        var g = course.gravitationalConstant();
        var kf = course.kineticFrictionGrass();
        var coef1 = Math.sqrt((2*kf)/Math.sqrt(2+dz_dx*dz_dx+dz_dy*dz_dy));
        var coef2 = g/(pyth(dz_dx,dz_dy,1));

        //var vel_z = coef1*coef2*Math.sqrt(dz*(dz_dx+dz_dy));
        var vel_x = coef1*coef2*Math.sqrt(dx);
        var vel_y = coef1*coef2*Math.sqrt(dy);

        return new StateVector4(initState.x(), initState.y(), vel_x, vel_y);
    }

    private static double pyth(double... nums) {
        double sum = 0;
        for(double num : nums) {
            sum += Math.pow(num, 2);
        }
        return Math.sqrt(sum);
    }

}
