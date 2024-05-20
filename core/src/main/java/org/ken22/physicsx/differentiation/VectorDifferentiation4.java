package org.ken22.physicsx.differentiation;

import org.ken22.physicsx.vectors.StateVector4;

import java.util.function.Function;

/**
 * This class is used to store the differentiation functions for a 4D vector representing the state of a solid in 2D space.
 * The functions are used to calculate the rate of change of the x, y, vx and vy components of the vector.
 */
public class VectorDifferentiation4 {
    private final Function<StateVector4, Double> dx;
    private final Function<StateVector4, Double> dy;
    private final Function<StateVector4, Double> dvx;
    private final Function<StateVector4, Double> dvy;

    public VectorDifferentiation4(Function<StateVector4, Double> dx, Function<StateVector4, Double> dy,
                                  Function<StateVector4, Double> dvx, Function<StateVector4, Double> dvy)
    {
        this.dx = dx;
        this.dy = dy;
        this.dvx = dvx;
        this.dvy = dvy;
    }

    @SuppressWarnings("unused")
    public double dx(StateVector4 sv) {
        return dx.apply(sv);
    }
    @SuppressWarnings("unused")
    public double dy(StateVector4 sv) {
        return dy.apply(sv);
    }
    public double dvx(StateVector4 sv) {
        return dvx.apply(sv);
    }
    public double dvy(StateVector4 sv) {
        return dvy.apply(sv);
    }

    public StateVector4 dsv(StateVector4 sv) {
        return new StateVector4(dx.apply(sv), dy.apply(sv), dvx.apply(sv), dvy.apply(sv));
    }
}
