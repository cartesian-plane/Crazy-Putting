package org.ken22.physics.odesolvers.inplace;

import java.util.function.BiConsumer;

public interface ODESolver<State> {
    public void solve(double timeStep, double endTime, State initialState, BiConsumer<Double, State> differentiation);
    public void nextStep(double timeStep, State currentState, BiConsumer<Double, State> differentiation);
}
