package org.ken22.odesolver;

import org.ken22.interfaces.ODESolution;
import org.ken22.odesolver.methods.ODESolverMethod;

/**
 * Context class for the Strategy pattern.
 */
public class ODESolver {
    /**
     * Concrete strategy(solver) to be used by the context class.
     */
    private ODESolverMethod strategy;

    public ODESolver(ODESolverMethod strategy) {
        this.strategy = strategy;
    }

    public ODESolver() {

    }

    /**
     * Set the Context's strategy.
     * @param strategy A concrete strategy (Euler, RK, etc.).
     */
    public void setStrategy(ODESolverMethod strategy) {
        this.strategy = strategy;
    }

    /**
     * Call the strategy's solve method.
     * Will call the solve method of the concrete strategy (Euler, RK, etc.).
     * The time taken, and accuracy of the solution will depend on the concrete strategy used.
     * @return {@link ODESolution} object containing the solution to the system of ODEs.
     */
    public ODESolution solve() {
        return strategy.solve();
    }
}
