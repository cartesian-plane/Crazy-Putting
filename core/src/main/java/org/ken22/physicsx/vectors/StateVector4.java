package org.ken22.physicsx.vectors;

public class StateVector4 {
    private double x;
    private double y;
    private double vx;
    private double vy;

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
}
