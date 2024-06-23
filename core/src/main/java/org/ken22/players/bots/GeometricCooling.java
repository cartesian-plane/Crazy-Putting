package org.ken22.players.bots;

public final class GeometricCooling implements Schedule {
    private final double initialTemperature;
    private final double alpha;

    public GeometricCooling(double initialTemperature, double alpha) {
        this.initialTemperature = initialTemperature;
        this.alpha = alpha;
    }

    @Override
    public double getNewTemperature(double t) {
        double T = Math.pow(alpha, t) * initialTemperature;
        return Math.max(T, 0);
    }
}
