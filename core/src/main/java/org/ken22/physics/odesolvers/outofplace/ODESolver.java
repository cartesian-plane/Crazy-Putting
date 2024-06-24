package org.ken22.physics.odesolvers.outofplace;

import java.util.function.BiFunction;

public interface ODESolver<State> {
    State solve(double timeStep, double endTime, State initialState, BiFunction<Double, State, State> differentiation);
    State nextStep(double timeStep, State currentState, BiFunction<Double, State, State> differentiation);
}
