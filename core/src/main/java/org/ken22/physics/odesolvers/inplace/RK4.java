package org.ken22.physics.odesolvers.inplace;

import org.ken22.physics.vectors.StateVector4;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class RK4 implements ODESolver<StateVector4> {

    @Override
    public void solve(double h, double endTime, StateVector4 initialState, BiFunction<Double, StateVector4, double[]> differentiation) {
        double t = 0;
        while (t < endTime) {
            nextStep(h, initialState, differentiation);
            t += h;
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void nextStep(double timeStep, StateVector4 currentState, BiFunction<Double, StateVector4, double[]> differentiation) {
        var initialValues = new double[] {currentState.x, currentState.y, currentState.vx, currentState.vy};
        var h = timeStep;

        var k1 = differentiation.apply(0.0, currentState);
        //prepare stateVec for k2
        currentState.x = initialValues[0] + k1[0] * h / 2;
        currentState.y = initialValues[1] + k1[1] * h / 2;
        currentState.vx = initialValues[2] + k1[2] * h / 2;
        currentState.vy = initialValues[3] + k1[3] * h / 2;

        var k2 = differentiation.apply(h / 2, currentState);
        //prepare stateVec for k3
        currentState.x = initialValues[0] + k2[0] * h / 2;
        currentState.y = initialValues[1] + k2[1] * h / 2;
        currentState.vx = initialValues[2] + k2[2] * h / 2;
        currentState.vy = initialValues[3] + k2[3] * h / 2;

        var k3 = differentiation.apply(h / 2, currentState);
        //prepare stateVec for k4
        currentState.x = initialValues[0] + k3[0] * h;
        currentState.y = initialValues[1] + k3[1] * h;
        currentState.vx = initialValues[2] + k3[2] * h;
        currentState.vy = initialValues[3] + k3[3] * h;

        var k4 = differentiation.apply(h, currentState);

        currentState.x = initialValues[0] + (k1[0]*h + 2*k2[0]*h + 2*k3[0]*h + k4[0]*h) / 6.0;
        currentState.y = initialValues[1] + (k1[1]*h + 2*k2[1]*h + 2*k3[1]*h + k4[1]*h) / 6.0;
        currentState.vx = initialValues[2] + (k1[2]*h + 2*k2[2]*h + 2*k3[2]*h + k4[2]*h) / 6.0;
        currentState.vy = initialValues[3] + (k1[3]*h + 2*k2[3]*h + 2*k3[3]*h + k4[3]*h) / 6.0;
    }
}
