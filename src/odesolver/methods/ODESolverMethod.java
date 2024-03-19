package odesolver.methods;

import interfaces.ODESolution;
import interfaces.ODESystem;

/**
 * Abstract class for the Strategy pattern.
 * Also holds the fields used by the concrete solvers.
 */
public abstract class ODESolverMethod {
    protected ODESystem system;
    protected double stepSize;
    protected double endPoint;

    /**
     * Initialize the ODE solver method.
     * All ODE Solver methods (Euler, RK, etc.) inherit the fields instantiated in this constructor.
     *
     * @param system   the system of equations to solve
     * @param stepSize step size used by the solver
     * @param endPoint the end point of the interval to solve, namely: [initialValue, endPoint]
     */
    public ODESolverMethod(ODESystem system, double stepSize, double endPoint) {
        this.system = system;
        this.stepSize = stepSize;
        this.endPoint = endPoint;
    }

    /**
     * Solve the system of ODEs.
     * This method is overridden by the concrete ODE solver methods (Euler, RK, etc.).
     * To be called by the context class (ODESolver).
     *
     * @return {@link  ODESolution} object containing the solution to the system of ODEs
     */
    public abstract ODESolution solve();
}
