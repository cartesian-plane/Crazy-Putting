package org.ken22.physics.odesolvers.inplace;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface ODESolver<State> {
    // the differentiation returns a double[4] of the derivative of the statevector at t + h
    public void solve(double timeStep, double endTime, State initialState, BiFunction<Double, State, double[]> differentiation);
    public void nextStep(double timeStep, State currentState, BiFunction<Double, State, double[]> differentiation);
}
