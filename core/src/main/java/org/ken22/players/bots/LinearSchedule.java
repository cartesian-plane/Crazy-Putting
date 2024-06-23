package org.ken22.players.bots;

@SuppressWarnings("ClassCanBeRecord")
public class LinearSchedule implements Schedule {

    public final double initialTemperature;
    public final double alpha;

    public LinearSchedule(double initialTemperature, double alpha) {
        this.initialTemperature = initialTemperature;
        this.alpha = alpha;
    }

    @Override
    public double getNewTemperature(double t) {
        double T = initialTemperature - alpha * t;
        return Math.max(T, 0);
    }
}
