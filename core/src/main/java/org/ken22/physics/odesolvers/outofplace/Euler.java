package org.ken22.physics.odesolvers.outofplace;

import org.ken22.physics.vectors.StateVector4;

import java.util.function.BiFunction;

//TODO: Implement the Euler class
public class Euler implements ODESolver<StateVector4> {
    @Override
    public StateVector4 solve(double timeStep, double endTime, StateVector4 initialState, BiFunction<Double, StateVector4, StateVector4> differentiation) {
        return null;
    }

    @Override
    public StateVector4 nextStep(double timeStep, StateVector4 currentState, BiFunction<Double, StateVector4, StateVector4> differentiation) {
        return null;
    }
}
