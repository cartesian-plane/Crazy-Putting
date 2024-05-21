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
        StateVector4 k1 = dif.apply(0.0, sv);
        StateVector4 k2 = dif.apply(h / 2, sv.add(k1.multiply(h / 2)));
        StateVector4 k3 = dif.apply(h / 2, sv.add(k2.multiply(h / 2)));
        StateVector4 k4 = dif.apply(h, sv.add(k3.multiply(h)));
        StateVector4 next = sv.add((k1.add(k2.multiply(2)).add(k3.multiply(2)).add(k4)).multiply(1 / 6));
        return next;
    }
}
