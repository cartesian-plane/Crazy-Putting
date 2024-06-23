package org.ken22.players.bots;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.PhysicsFactory;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.Player;
import org.ken22.players.error.ErrorFunction;
import org.ken22.utils.MathUtils;
import org.ken22.utils.MatrixUtils;

public class NewtonRaphsonBot implements Player {
    private double stepSize;

    private StateVector4 currentState;

    private ErrorFunction errorFunction;
    private double maxIterations = 100;
    private double tolerance = 1e-1;

    private double[][] hessian = new double[2][2];
    private double[] gradient = new double[2];
    private double hessianDet;

    public NewtonRaphsonBot(ErrorFunction errorFunction, double stepSize) {
        this.stepSize = stepSize;
        this.errorFunction = errorFunction;
    }

    public NewtonRaphsonBot(ErrorFunction errorFunction, double stepSize, double maxIterations, double tolerance) {
        this.stepSize = stepSize;
        this.errorFunction = errorFunction;
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
    }

    @Override
    public StateVector4 play(StateVector4 state) {
        currentState = state;
        for(int i = 0; i < maxIterations; i++) { // Newton-Raphson iteration
            hessianAndGradient3Point(currentState, errorFunction, stepSize);
            if (hessianDet == 0) {
                System.out.println("Hessian determinant is zero, cannot invert");
                return currentState;
            }
            double[] delta = MatrixUtils.transform(MatrixUtils.inverse(hessian), gradient);
            currentState = new StateVector4(currentState.x(), currentState.y(), currentState.vx() - delta[0], currentState.vy() - delta[1]);
            if(MathUtils.magnitude(delta) < tolerance) { // Converged
                break;
            }
        }

        return currentState;
    }

    private void hessianAndGradient3Point(StateVector4 currentState, ErrorFunction errorFunction, double stepSize) {
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

        hessianDet = hessian[0][0] * hessian[1][1] - hessian[0][1] * hessian[1][0];

        gradient[0] = (e10 - e_10) / (2 * stepSize);
        gradient[1] = (e01 - e0_1) / (2 * stepSize);
    }
}
