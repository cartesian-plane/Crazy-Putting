package org.ken22.players.bots;

public class LogarithmicCooling implements Schedule{
    private final double initialTemperature;
    private final double alpha;


    public LogarithmicCooling(double initialTemperature, double alpha) {
        this.initialTemperature = initialTemperature;
        this.alpha = alpha;
    }

    @Override
    public double getNewTemperature(double t) {
        return ((alpha * initialTemperature) / Math.log(1 + t));
    }
}
