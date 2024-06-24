package org.ken22.physics.odesolvers.outofplace;

import org.ken22.physics.vectors.StateVector4;

import java.util.function.BiFunction;

public class RK2 implements ODESolver<StateVector4> {
    @Override
    public StateVector4 solve(double h, double endTime, StateVector4 initialState, BiFunction<Double, StateVector4, StateVector4> differentiation) {
        StateVector4 sv = initialState;
        double currentTime = 0.0;
        while (currentTime < endTime) {
            sv = nextStep(h, sv, differentiation);
            currentTime += h;
        }
        return sv;
    }

    @Override
    public StateVector4 nextStep(double h, StateVector4 sv, BiFunction<Double, StateVector4, StateVector4> differentiation) {
        StateVector4 k1_h = differentiation.apply(0.0, sv);
        StateVector4 k2_h = differentiation.apply(h/2.0, sv.add(k1_h.multiply(h/2.0)));
        return (sv.add(k1_h.add(k2_h)).multiply(h / 2.0));
    }
}
