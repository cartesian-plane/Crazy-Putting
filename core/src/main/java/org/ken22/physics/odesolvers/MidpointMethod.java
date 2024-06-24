package org.ken22.physics.odesolvers;

import org.ken22.physics.vectors.StateVector4;

import java.util.function.BiFunction;

public class MidpointMethod implements ODESolver<StateVector4> {

    @Override
    public StateVector4 solve(double h, double ft, StateVector4 sv0, BiFunction<Double, StateVector4, StateVector4> dif) {
        StateVector4 svi = sv0;
        double t = 0;
        while (t < ft) {
            svi = nextStep(h, svi, dif);
            t += h;
        }
        return svi;
    }

    @Override
    public StateVector4 nextStep(double h, StateVector4 sv, BiFunction<Double, StateVector4, StateVector4> dif) {
        StateVector4 k1_h = dif.apply(0.0, sv);
        StateVector4 k2_h = dif.apply(h/2, sv.add(k1_h.multiply(h / 2)));
        return sv.add(k2_h.multiply(h));
    }
}
