package org.ken22.players.bots;

public final class GeometricCooling implements Schedule {
    private final double initialTemperature;
    private final double alpha;

    public GeometricCooling(double initialTemperature, double alpha) {
        this.initialTemperature = initialTemperature;
        this.alpha = alpha;
    }

    @Override
    public double getNewTemperature(int k) {
        double T = Math.pow(alpha, k) * initialTemperature;
        if (T < 0.0001) {
            return 0;
        } else {
            return T;
        }
    }
}
