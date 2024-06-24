package org.ken22.players.bots.simulatedannealing;

import org.ken22.players.bots.Schedule;

public class LogarithmicCooling implements Schedule {
    private final double initialTemperature;
    private final double c;


    public LogarithmicCooling(double initialTemperature, double c) {
        this.initialTemperature = initialTemperature;
        this.c = c;
    }

    @Override
    public double getNewTemperature(int k) {
        return (c / Math.log(1 + k));
    }
}
