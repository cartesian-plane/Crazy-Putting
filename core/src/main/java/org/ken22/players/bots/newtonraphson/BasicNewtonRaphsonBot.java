package org.ken22.players.bots.newtonraphson;

import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.Player;
import org.ken22.players.error.ErrorFunction;
import org.ken22.utils.MathUtils;

public class BasicNewtonRaphsonBot implements Player {
    private double stepSize;

    private StateVector4 currentState;

    private Player initialGuessBot;
    private ErrorFunction errorFunction;
    private double maxIterations;
    private double tolerance;
    private double errorThreshold = 0.05;

    private double[] gradient = new double[2];

    public BasicNewtonRaphsonBot(Player initialGuessBot, ErrorFunction errorFunction, double stepSize) {
        this(initialGuessBot, errorFunction, stepSize, 250, 1e-6);
    }

    public BasicNewtonRaphsonBot(Player initialGuessBot, ErrorFunction errorFunction, double stepSize, double maxIterations, double tolerance) {
        this.initialGuessBot = initialGuessBot;
        this.stepSize = stepSize;
        this.errorFunction = errorFunction;
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
    }

    @Override
    public StateVector4 play(StateVector4 state) {
        if (initialGuessBot != null) {
            currentState = initialGuessBot.play(state);
        } else {
            currentState = state;
        }

        for(int i = 0; i < maxIterations; i++) { // Newton-Raphson iteration
            gradient5Point(currentState, errorFunction, stepSize);
            var error = errorFunction.calculateError(currentState);
            double[] delta = new double[] {error / gradient[0], error / gradient[1]};
            System.out.println("Delta: [" + delta[0] + ", " + delta[1] + "]");
            currentState = new StateVector4(currentState.x(), currentState.y(), currentState.vx() - delta[0], currentState.vy() - delta[1]);
            if(MathUtils.magnitude(delta) < tolerance) { // Converged
                break;
            } else if (errorFunction.calculateError(currentState) < errorThreshold) {
                break;
            }
        }

        return currentState;
    }

    private void gradient5Point(StateVector4 currentState, ErrorFunction errorFunction, double stepSize) {

        var e10 = errorFunction.calculateError(new StateVector4(currentState.x(), currentState.y(), currentState.vx() + stepSize, currentState.vy()));
        var e_10 = errorFunction.calculateError(new StateVector4(currentState.x(), currentState.y(), currentState.vx() - stepSize, currentState.vy()));
        var e01 = errorFunction.calculateError(new StateVector4(currentState.x(), currentState.y(), currentState.vx(),currentState.vy() + stepSize));
        var e0_1 = errorFunction.calculateError(new StateVector4(currentState.x(), currentState.y(), currentState.vx(),currentState.vy() - stepSize));

        var e20 = errorFunction.calculateError(new StateVector4(currentState.x(), currentState.y(), currentState.vx() + 2 * stepSize, currentState.vy()));
        var e_20 = errorFunction.calculateError(new StateVector4(currentState.x(), currentState.y(), currentState.vx() - 2 * stepSize, currentState.vy()));
        var e02 = errorFunction.calculateError(new StateVector4(currentState.x(), currentState.y(), currentState.vx(), currentState.vy() + 2 * stepSize));
        var e0_2 = errorFunction.calculateError(new StateVector4(currentState.x(), currentState.y(), currentState.vx(), currentState.vy() - 2 * stepSize));

        gradient[0] = (e_20 + 8*e_10 - 8*e10 - e20) / (12 * stepSize);
        gradient[1] = (e0_2 + 8*e0_1 - 8*e01 - e02) / (12 * stepSize);

        System.out.println(currentState);
        System.out.println("Gradient: [" + gradient[0] + ", " + gradient[1] + "]");
    }
}
