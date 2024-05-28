package org.ken22.physicsx.odesolvers;

import org.ken22.physicsx.differentiation.VectorDifferentiation4;
import org.ken22.physicsx.vectors.StateVector4;

import java.util.function.BiFunction;

public class RK4 implements ODESolver<StateVector4> {
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
        StateVector4 k2_h = dif.apply(h / 2, sv.add(k1_h.multiply(h / 2)));
        StateVector4 k3_h = dif.apply(h / 2, sv.add(k2_h.multiply(h / 2)));
        StateVector4 k4_h = dif.apply(h, sv.add(k3_h.multiply(h)));
        StateVector4 next = sv.add((k1_h.add(k2_h.multiply(2)).add(k3_h.multiply(2)).add(k4_h)).multiply(h / 6.0));
        return next;
    }
}
