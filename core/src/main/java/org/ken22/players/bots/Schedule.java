package org.ken22.players.bots;

@FunctionalInterface
public interface Schedule {
    /**
     * Return the new temperature, updated as per the schedule function.
     * @param t the current time
     * @return the new temperature
     */
    double getNewTemperature(double t);
}
