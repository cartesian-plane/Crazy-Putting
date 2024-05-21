package org.ken22.physicsx.vectors;

@SuppressWarnings("ClassCanBeRecord")
public class StateVector4 {
    private final double x;
    private final double y;
    private final double vx;
    private final double vy;

    public StateVector4(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public double x() {
        return x;
    }
    public double y() {
        return y;
    }
    public double vx() {
        return vx;
    }
    public double vy() {
        return vy;
    }

    public StateVector4 add(StateVector4 sv) {
        return new StateVector4(x + sv.x, y + sv.y, vx + sv.vx, vy + sv.vy);
    }
    public StateVector4 multiply(double scalar) {
        return new StateVector4(x * scalar, y * scalar, vx * scalar, vy * scalar);
    }

    public String toString() {
        return "x: " + x + ", y: " + y + ", vx: " + vx + ", vy: " + vy;
    }
}
