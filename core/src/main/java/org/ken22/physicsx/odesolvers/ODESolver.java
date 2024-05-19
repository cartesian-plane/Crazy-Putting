package org.ken22.physicsx.odesolvers;

import org.ken22.physicsx.vectors.StateVector4;

public interface ODESolver {
    StateVector4 solve(double timeStep, double endTime, StateVector4 initialState, Derivation4 derivation);
    StateVector4 nextStep(double timeStep, StateVector4 currentState, Derivation4 derivation);
}
