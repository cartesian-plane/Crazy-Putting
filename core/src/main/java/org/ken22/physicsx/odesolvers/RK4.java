package org.ken22.physicsx.odesolvers;

import org.ken22.physicsx.differentiation.VectorDifferentiation4;
import org.ken22.physicsx.vectors.StateVector4;

public class RK4 implements ODESolver {
    @Override
    public StateVector4 solve(double h, double ft, StateVector4 sv0, VectorDifferentiation4 dif) {
        StateVector4 svi = sv0;
        double t = 0;
        while (t < ft) {
            svi = nextStep(h, svi, dif);
            t += h;
        }
        return svi;
    }

    @Override
    public StateVector4 nextStep(double h, StateVector4 sv, VectorDifferentiation4 dif) {
        double x = sv.x();
        double y = sv.y();
        double vx = sv.vx();
        double vy = sv.vy();

        double k1x = h * vx;
        double k1y = h * vy;
        double k1vx = h * dif.dvx(sv);
        double k1vy = h * dif.dvy(sv);

        double k2x = h * (vx + 0.5 * k1vx);
        double k2y = h * (vy + 0.5 * k1vy);
        double k2vx = h * dif.dvx(new StateVector4(x + 0.5 * k1x, y + 0.5 * k1y, vx + 0.5 * k1vx, vy + 0.5 * k1vy));
        double k2vy = h * dif.dvy(new StateVector4(x + 0.5 * k1x, y + 0.5 * k1y, vx + 0.5 * k1vx, vy + 0.5 * k1vy));

        double k3x = h * (vx + 0.5 * k2vx);
        double k3y = h * (vy + 0.5 * k2vy);
        double k3vx = h * dif.dvx(new StateVector4(x + 0.5 * k2x, y + 0.5 * k2y, vx + 0.5 * k2vx, vy + 0.5 * k2vy));
        double k3vy = h * dif.dvy(new StateVector4(x + 0.5 * k2x, y + 0.5 * k2y, vx + 0.5 * k2vx, vy + 0.5 * k2vy));

        double k4x = h * (vx + k3vx);
        double k4y = h * (vy + k3vy);
        double k4vx = h * dif.dvx(new StateVector4(x + k3x, y + k3y, vx + k3vx, vy + k3vy));
        double k4vy = h * dif.dvy(new StateVector4(x + k3x, y + k3y, vx + k3vx, vy + k3vy));

        double new_x = x + (1.0 / 6.0) * (k1x + 2 * k2x + 2 * k3x + k4x);
        double new_y = y + (1.0 / 6.0) * (k1y + 2 * k2y + 2 * k3y + k4y);
        double new_vx = vx + (1.0 / 6.0) * (k1vx + 2 * k2vx + 2 * k3vx + k4vx);
        double new_vy = vy + (1.0 / 6.0) * (k1vy + 2 * k2vy + 2 * k3vy + k4vy);

        return new StateVector4(new_x, new_y, new_vx, new_vy);
    }
}
