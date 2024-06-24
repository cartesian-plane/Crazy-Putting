package org.ken22.players.bots;

@SuppressWarnings("ClassCanBeRecord")
public class LinearCooling implements Schedule {

    public final double initialTemperature;
    public final double alpha;

    /**
     * Initialise the linear schedule parameters.
     * @param initialTemperature T<sub>0</sub>
     * @param alpha the cooling rate
     */
    public LinearCooling(double initialTemperature, double alpha) {
        this.initialTemperature = initialTemperature;
        this.alpha = alpha;
    }

    /**
     * <p>A simple linear schedule for updating the temperature.</p>
     * <p>The parameter {@code alpha} controls the cooling rate.</p>
     *
     * <p><b>References: </b></p>
     * <ul>
     *     <li>Artificial Intelligence: A Modern Approach 3rd ed. (Chapter 4)</li>
     *     <li>Nourani, Y., & Andresen, B. (1998). A comparison of simulated annealing cooling strategies.
     *     Journal of Physics. A, Mathematical and General/Journal of Physics. A, Mathematical and General, 31(41),
     *     8373–8385. <a href="https://doi.org/10.1088/0305-4470/31/41/011">https://doi.org/10.1088/0305-4470/31/41/011</a></li>
     *
     * </ul>
     */
    @Override
    public double getNewTemperature(int k) {
        // k is the iteration number
        double T = initialTemperature - alpha * k;
        return Math.max(T, 0);
    }
}
