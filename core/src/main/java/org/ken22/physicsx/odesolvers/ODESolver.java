package org.ken22.physicsx.odesolvers;

import org.ken22.physicsx.differentiation.VectorDifferentiation4;
import org.ken22.physicsx.vectors.StateVector4;

import java.util.function.BiFunction;

public interface ODESolver<State> {
    StateVector4 solve(double timeStep, double endTime, State initialState, BiFunction<Double, State, State> differentiation);
    StateVector4 nextStep(double timeStep, State currentState, BiFunction<Double, State, State> differentiation);
}
