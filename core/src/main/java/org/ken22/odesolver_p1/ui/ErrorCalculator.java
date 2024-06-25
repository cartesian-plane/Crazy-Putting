import java.util.ArrayList;
import java.util.function.BiFunction;

/**
 * Calculates errors between numerical and analytical solutions for ODEs.
 */
public class ErrorCalculator {
    private final BiFunction<Double, StateVector4, StateVector4> odeFunction;
    private final BiFunction<Double, Double, Double> analyticalSolutionFunction;
    private final ODESolver<StateVector4> odeSolver;
    private final StateVector4 initialState;
    private final double stepSize;
    private final double endTime;
    private final double stepSizeIncrease;
    private final ArrayList<Double> stepSizeList = new ArrayList<>();

    /**
     * Constructs a new ErrorCalculator.
     * @param odeFunction The ODE function.
     * @param odeSolver The ODE solver.
     * @param initialState The initial state.
     * @param stepSize The initial step size.
     * @param endTime The end time.
     * @param stepSizeIncrease The step size increase.
     * @param analyticalSolutionFunction The analytical solution function.
     */
    public ErrorCalculator(BiFunction<Double, StateVector4, StateVector4> odeFunction, ODESolver<StateVector4> odeSolver,
                           StateVector4 initialState, double stepSize, double endTime, double stepSizeIncrease, 
                           BiFunction<Double, Double, Double> analyticalSolutionFunction) {
        this.odeFunction = odeFunction;
        this.odeSolver = odeSolver;
        this.initialState = initialState;
        this.stepSize = stepSize;
        this.endTime = endTime;
        this.stepSizeIncrease = stepSizeIncrease;
        this.analyticalSolutionFunction = analyticalSolutionFunction;
    }

    /**
     * Calculates errors between numerical and analytical solutions.
     * @return The list of errors.
     */
    public ArrayList<Double> calculateError() {
        ArrayList<StateVector4> numericalSolutions = new ArrayList<>();
        ArrayList<Double> analyticalSolutions = new ArrayList<>();
        double currentStepSize = stepSize;

        while (currentStepSize <= endTime) {
            StateVector4 finalSolution = odeSolver.solve(currentStepSize, endTime, initialState, odeFunction);
            double finalStepSize = getLastStepSize(currentStepSize, endTime, initialState);
            double analyticalSolution = analyticalSolutionFunction.apply(finalStepSize, initialState.y());
            numericalSolutions.add(finalSolution);
            analyticalSolutions.add(analyticalSolution);
            stepSizeList.add(currentStepSize);
            currentStepSize += stepSizeIncrease;
        }

        ArrayList<Double> errors = new ArrayList<>();
        for (int i = 0; i < numericalSolutions.size(); i++) {
            double error = calculateError(numericalSolutions.get(i), analyticalSolutions.get(i));
            errors.add(error);
        }

        return errors;
    }

    /**
     * Gets the list of step sizes used.
     * @return The list of step sizes.
     */
    public ArrayList<Double> getStepSizeList() {
        return stepSizeList;
    }

    /**
     * Gets the last step size used.
     * @param h The step size.
     * @param endTime The end time.
     * @param initialState The initial state.
     * @return The last step size.
     */
    private double getLastStepSize(double h, double endTime, StateVector4 initialState) {
        double t = initialState.x();

        while (t < endTime) {
            t += h;
        }

        return t ;
    }

    /**
     * Calculates the error between the numerical and analytical solutions.
     * @param numericalSolution The numerical solution.
     * @param analyticalValue The analytical value.
     * @return The error.
     */
    private double calculateError(StateVector4 numericalSolution, double analyticalValue) {
        return Math.abs(numericalSolution.y() - analyticalValue);
    }
}
