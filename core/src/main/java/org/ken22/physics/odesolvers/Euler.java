package org.ken22.physics.odesolvers;

import org.ken22.physics.vectors.StateVector4;

import java.util.function.BiFunction;

//TODO: Implement the Euler class
public class Euler implements ODESolver<StateVector4> {
    @Override
    public StateVector4 solve(double h, double endTime, StateVector4 initialState, BiFunction<Double, StateVector4, StateVector4> dif) {
        StateVector4 sv = initialState;
        double currentTime = 0;
        while (currentTime < endTime) {
            sv = nextStep(h, sv, dif);
            currentTime += h;
        }
        return sv;
    }

    @Override
    public StateVector4 nextStep(double h, StateVector4 sv, BiFunction<Double, StateVector4, StateVector4> dif) {
      return sv.add(dif.apply(0.0, sv).multiply(h));
    }
}
