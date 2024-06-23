package org.ken22.players.bots.hillclimbing;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.differentiators.Differentiator;
import org.ken22.physics.odesolvers.ODESolver;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.Player;
import org.ken22.players.bots.GeometricCooling;
import org.ken22.players.bots.LogarithmicCooling;
import org.ken22.players.bots.Schedule;
import org.ken22.players.error.ErrorFunction;

import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>This class contains an implementation of simulated annealing.</p>
 *
 * <p>Various schedule functions are supported, via the {@link Schedule} interface.</p>
 *
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
public final class SimulatedAnnealing implements Player {
    private static final Logger LOGGER = Logger.getLogger(HillClimber.class.getName());

    static {

        // the default level is INFO
        // if you want to change logging, just change the enum type at (1) and (2)
        // https://docs.oracle.com/javase/8/docs/api/java/util/logging/Level.html
        LOGGER.setLevel(Level.FINER); // (1)


        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE); // (2)
        LOGGER.addHandler(consoleHandler);
    }

    private final double DELTA;
    private final double THRESHOLD;
    private final double initialTemperature;
    private final double allottedTime;
    private final Schedule schedule;
    private final GolfCourse course;
    private final ODESolver<StateVector4> solver;
    private final Differentiator differentiator;
    private final double stepSize;

    private final ErrorFunction heuristicFunction;
    private final Evaluator evaluator;

    public SimulatedAnnealing(GolfCourse course,
                              ODESolver<StateVector4> solver,
                              Differentiator differentiator,
                              double stepSize,
                              double initialTemperature,
                              double allottedTime,
                              ErrorFunction errorFunction) {
        this.course = course;
        this.solver = solver;
        this.differentiator = differentiator;
        this.stepSize = stepSize;
        this.DELTA = 0.01;
        this.THRESHOLD = course.targetRadius;
        this.heuristicFunction = errorFunction;
        this.evaluator = new Evaluator(this.heuristicFunction, this.course, this.solver, this.differentiator,
            this.stepSize);

        this.initialTemperature = 1000;
        this.allottedTime = allottedTime;
        this.schedule = new GeometricCooling(initialTemperature, 0.95);
    }

    public StateVector4 play(StateVector4 state) {
        return search(state);
    }

    private StateVector4 search(StateVector4 state) {
        // flag that stores whether a solution was found
        // if the search stops before a solution is found, a logging message is displayed
        boolean foundSolution = false;
        var current = state;
        // simulated annealing
        for (double t = 0; t < allottedTime; t += 0.1) {
            double temperature = schedule.getNewTemperature(t);
            System.out.println("temperature = " + temperature);
            if (temperature < 0.001) {
                LOGGER.log(Level.INFO, "Temperature reached 0, returning current state");
                System.out.println("current = " + current);
                return current;
            }

            var next = getRandomNeighbour(current);
            var nextValue = evaluator.evaluateState(next);

            // check if a solution is reached
            if (nextValue < THRESHOLD) {
                current = next;
                foundSolution = true;
                break;
            }

            var deltaE = nextValue - evaluator.evaluateState(current);


            if (deltaE < 0) {
                current = next; // if the evaluation is improved, accept immediately
            } else {
                System.out.println("deltaE = " + deltaE);
                double probability = Math.exp(-deltaE / temperature);
                System.out.println("probability = " + probability);
                if (Math.random() < probability) {
                    current = next;
                }
            }
        }

        String message;
        if (foundSolution) {
            message = "Found solution! " + "vx: " + current.vx() + ", vy: " + current.vy();
        } else {
            message = "Solution not found but allotted time ran out";
        }
        LOGGER.log(Level.INFO, message);

        System.out.println("RETURNING current = " + current);
        return current;
    }


    private StateVector4 getRandomNeighbour(StateVector4 currentState) {
//
//        final double initialX = currentState.x();
//        final double initialY = currentState.y();
//
//        // we are in a 2-d space, so we have 4 neighbours
//        var neighbour1 = new StateVector4(initialX, initialY,
//            currentState.vx() + DELTA, currentState.vy());
//        var neighbour2 = new StateVector4(initialX, initialY,
//            currentState.vx() - DELTA, currentState.vy());
//
//
//        var neighbour3 = new StateVector4(initialX, initialY,
//            currentState.vx(), currentState.vy() + DELTA);
//        var neighbour4 = new StateVector4(initialX, initialY,
//            currentState.vx(), currentState.vy() - DELTA);
//
//        ArrayList<StateVector4> neighbours = new ArrayList<>();
//        neighbours.add(neighbour1);
//        neighbours.add(neighbour2);
//        neighbours.add(neighbour3);
//        neighbours.add(neighbour4);
//
//        // ensures a random neighbour is always returned
//        Collections.shuffle(neighbours);
//        return neighbours.getFirst();

        final double initialVx = currentState.vx();
        final double initialVy = currentState.vy();

        double vx = initialVx + DELTA * ((Math.random() - 0.5) * 2);
        double vy = initialVy + DELTA * ((Math.random() - 0.5) * 2);

        return new StateVector4(currentState.x(), currentState.y(), vx, vy);
    }

    /**
     * <p>Generate a random speed vector.</p>
     * Ensures that the magnitude of the vector does not exceed 5m/s
     *
     * @return randomly generated speed vector (vx, vy)
     */
    private double[] getRandomSpeedVector() {
        double[] vector = new double[2];
        Random random = new Random();
        vector[0] = random.nextDouble() * 5 * 2 - 5; // random number between -5 and 5
        double x = Math.sqrt(25-vector[0]*vector[0]);
        vector[1] = random.nextDouble()*2*x-x; // random number between -5 and 5
        return vector;

    }
}
