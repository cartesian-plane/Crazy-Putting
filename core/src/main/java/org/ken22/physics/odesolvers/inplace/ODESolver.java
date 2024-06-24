package org.ken22.physics.odesolvers.inplace;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface ODESolver<State> {
    public void solve(double timeStep, double endTime, State initialState, BiFunction<Double, State, double[]> differentiation); //
    public void nextStep(double timeStep, State currentState, BiFunction<Double, State, double[]> differentiation);
}
