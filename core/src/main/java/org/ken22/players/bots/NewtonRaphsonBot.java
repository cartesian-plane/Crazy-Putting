package org.ken22.players.bots;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.PhysicsFactory;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.Player;
import org.ken22.players.error.ErrorFunction;

public class NewtonRaphsonBot implements Player {
    private GolfCourse course;
    private Expression expr;

    private PhysicsFactory physicsFactory;
    private double stepSize;

    private ErrorFunction errorFunction;

    public NewtonRaphsonBot(GolfCourse course, PhysicsFactory physicsFactory, double stepSize, ErrorFunction errorFunction) {
        this.course = course;
        this.expr = course.expression;
        this.physicsFactory = physicsFactory;
        this.stepSize = stepSize;
        this.errorFunction = errorFunction;
    }

    @Override
    public StateVector4 play(StateVector4 state) {
        return null;
    }

    private static double[][] hessian3Point(StateVector4 currentState, ErrorFunction errorFunction, double stepSize) {
        double[][] hessian = new double[2][2];

        var e00 = errorFunction.calculateError(currentState);

        var e10 = errorFunction.calculateError(new StateVector4(currentState.x() + stepSize, currentState.y(), currentState.vx(), currentState.vy()));
        var e_10 = errorFunction.calculateError(new StateVector4(currentState.x() - stepSize, currentState.y(), currentState.vx(), currentState.vy()));
        var e01 = errorFunction.calculateError(new StateVector4(currentState.x(), currentState.y() + stepSize, currentState.vx(), currentState.vy()));
        var e0_1 = errorFunction.calculateError(new StateVector4(currentState.x(), currentState.y() - stepSize, currentState.vx(), currentState.vy()));

        var e11 = errorFunction.calculateError(new StateVector4(currentState.x() + stepSize, currentState.y() + stepSize, currentState.vx(), currentState.vy()));
        //var e1_1 = errorFunction.calculateError(new StateVector4(currentState.x() + stepSize, currentState.y() - stepSize, currentState.vx(), currentState.vy()));
        //var e_11 = errorFunction.calculateError(new StateVector4(currentState.x() - stepSize, currentState.y() - stepSize, currentState.vx(), currentState.vy()));
        var e_1_1 = errorFunction.calculateError(new StateVector4(currentState.x() - stepSize, currentState.y() + stepSize, currentState.vx(), currentState.vy()));

        var e20 = errorFunction.calculateError(new StateVector4(currentState.x() + 2 * stepSize, currentState.y(), currentState.vx(), currentState.vy()));
        var e_20 = errorFunction.calculateError(new StateVector4(currentState.x() - 2 * stepSize, currentState.y(), currentState.vx(), currentState.vy()));
        var e02 = errorFunction.calculateError(new StateVector4(currentState.x(), currentState.y() + 2 * stepSize, currentState.vx(), currentState.vy()));
        var e0_2 = errorFunction.calculateError(new StateVector4(currentState.x(), currentState.y() - 2 * stepSize, currentState.vx(), currentState.vy()));

        // 3 point centered differences notes:
//        var fx10 = (e20 - e00) / (2 * stepSize);
//        var fx_10 = (e00 - e_20) / (2 * stepSize);
//        var fxx00 = (e20 - 2 * e00 + e_20) / (4 * stepSize * stepSize);
//
//        var fy10 = (e11 - e1_1) / (2 * stepSize);
//        var fy_10 = (e_11 - e_1_1) / (2 * stepSize);
//        var fyx00 = (e11 -  e1_1 - e_11 + e_1_1) / (4 * stepSize * stepSize);
//
//        var fy01 = (e02 - e00) / (2 * stepSize);
//        var fy0_1 = (e00 - e0_2) / (2 * stepSize);
//        var fyy00 = (e02 - 2 * e00 + e0_2) / (4 * stepSize * stepSize);
//
//        var fx01 = (e11 - e_11) / (2 * stepSize);
//        var fx0_1 = (e1_1 - e_1_1) / (2 * stepSize);
//        var fxy00 = (e11 - e_11 - e1_1 + e_1_1) / (4 * stepSize * stepSize);

        hessian[0][0] = (e20 - 2 * e00 + e_20) / (4 * stepSize * stepSize);
        hessian[0][1] = (e11 - e_1_1 - e10 + e_10) / (4 * stepSize * stepSize);
        hessian[1][0] = hessian[0][1];
        hessian[1][1] = (e02 - 2 * e00 + e0_2) / (4 * stepSize * stepSize);

        return hessian;
    }
}
