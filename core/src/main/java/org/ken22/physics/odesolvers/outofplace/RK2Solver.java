//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.BiFunction;
//
///**
// * Implements the second-order Runge-Kutta (RK2) method for solving ODEs.
// */
//public class RK2Solver implements ODESolver<StateVector4> {
//
//    /**
//     * Solves the ODE using the RK2 method.
//     * @param h The time step.
//     * @param endTime The end time.
//     * @param initialState The initial state.
//     * @param differentiation The differentiation function.
//     * @return The final state after solving the ODE.
//     */
//    @Override
//    public StateVector4 solve(double h, double endTime, StateVector4 initialState, BiFunction<Double, StateVector4, StateVector4> differentiation) {
//        List<StateVector4> results = new ArrayList<>();
//        results.add(initialState);  // Initial condition
//
//        StateVector4 currentState = initialState;
//        double t = initialState.x();
//
//        while (t < endTime) {
//            currentState = nextStep(h, currentState, differentiation);
//            t += h;
//            results.add(currentState);
//        }
//
//        return results.get(results.size() - 1);
//    }
//
//    /**
//     * Computes the next state using the RK2 method.
//     * @param timeStep The time step.
//     * @param currentState The current state.
//     * @param differentiation The differentiation function.
//     * @return The next state.
//     */
//    @Override
//    public StateVector4 nextStep(double timeStep, StateVector4 currentState, BiFunction<Double, StateVector4, StateVector4> differentiation) {
//        double t = currentState.x();
//        StateVector4 k1 = differentiation.apply(t, currentState);
//        StateVector4 k2 = differentiation.apply(t + timeStep, currentState.add(k1.multiply(timeStep)));
//        return currentState.add(k1.add(k2).multiply(timeStep / 2));
//    }
//}
