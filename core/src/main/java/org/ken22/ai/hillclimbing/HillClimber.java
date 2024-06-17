package org.ken22.ai.hillclimbing;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.engine.PhysicsEngine;
import org.ken22.physics.vectors.StateVector4;

import java.util.*;
import java.util.function.Function;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.ken22.physics.utils.PhysicsUtils.magnitude;

/**
 * <p>This class contains a simple steepest-descent hill-climbing, for achieving a hole-in one</p>
 *
 * <p>The state space is discretized, and the best neighbours are followed.</p>
 * <p>To avoid getting stuck in a plateau, a certain amount of sideways moves are allowed.</p>
 * <p>If the maximum amount of sideways moves is reached, the search is stopped and the best solution returned.</p>
 *
 *
 * <p><b>Note: </b></p>
 * <ul>
 *     Reference: <i>Artificial Intelligence: A Modern Approach 3rd ed. (Chapter 4)</i>
 * </ul>
 */
@SuppressWarnings({"unused", "DuplicatedCode"})
public class HillClimber {

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
    private final int MAX_SIDEWAYS_MOVES;
    private final GolfCourse course;
    private final StateVector4 initialState;

    public HillClimber(GolfCourse course) {
        this.course = course;
        this.DELTA = 0.01;
        this.THRESHOLD = course.targetRadius();
        this.MAX_SIDEWAYS_MOVES = 10;
        var initialX = course.ballX();
        var initialY = course.ballY();

        // choose random speed vector to start with
        var speedVector = getRandomSpeedVector();
        initialState = new StateVector4(initialX, initialY, speedVector[0], speedVector[1]);
    }

    public HillClimber(GolfCourse course, StateVector4 initialState) {
        this.course = course;
        this.DELTA = 0.01;
        this.THRESHOLD = course.targetRadius();
        this.MAX_SIDEWAYS_MOVES = 10;
        this.initialState = initialState;
    }

    /**
     * <p>Performs steepest-descent hill-climbing with sideways moves, to find a hole-in-one solution.</p>
     *
     * <p>If too many sideways moves are made, the search will stop.</p>
     *
     *
     * <p><b>Note: </b></p>
     * <ul>
     *     Hill-climbing is an anytime algorithm and the solution returned may not always achieve a hole-in-one
     *     for all cases.
     * </ul>
     *
     * @return the best solution found so far (at the moment of stopping)
     */
    public StateVector4 search() {

        // flag that stores whether a solution was found
        // if the search stops before a solution is found, a logging message is displayed
        boolean foundSolution = false;

        var currentState = initialState;

        int sidewaysMoves = 0;

        while (sidewaysMoves < MAX_SIDEWAYS_MOVES) {
            var neighbours = generateNeighbours(currentState);
            var neighbourEvaluations = evaluateNeighbours(neighbours);
            var bestNeighbour = Collections.max(neighbourEvaluations.entrySet(), Map.Entry.comparingByValue()).getKey();

            double bestNeighbourValue = neighbourEvaluations.get(bestNeighbour);


            double currentStateValue = evaluateState(currentState);

            if (bestNeighbourValue <= currentStateValue) {
                sidewaysMoves = 0;
            } else {
                sidewaysMoves += 1;
            }
            currentState = bestNeighbour;


            // check if a solution is reached
            if (bestNeighbourValue < THRESHOLD) {
                foundSolution = true;
                break;
            }
        }

        if (foundSolution) {
            var message = "Found solution! " + "vx: " + currentState.vx() + ", vy: " + currentState.vy();
            LOGGER.log(Level.INFO, message);
        } else {
            var message = "Stopped early! " + "vx: " + currentState.vx() + ", vy: " + currentState.vy();
            LOGGER.log(Level.WARNING, message);
        }

        return currentState;
    }

    /**
     * <p>Generates a given state's neighbours by discretizing its neighborhood.</p>
     *
     * <p>The state space is discretized by changing either one, or both of the vector components
     * by a fixed amount &plusmn;δ.</p>
     * <p>In the case of the 2-d search space for this problem, 4 neighbours will be generated.</p>
     *
     * <p><b>Note: </b></p>
     * <ul>
     *     Reference: <i>Artificial Intelligence: A Modern Approach 3rd ed. (Chapter 4)</i>
     * </ul>
     * @param currentState the state to generate the neighbours of
     * @return {@link List} of the state's neighbours
     */
    private List<StateVector4> generateNeighbours(StateVector4 currentState) {

        final double initialX = currentState.x();
        final double initialY = currentState.y();

        // we are in a 2-d space, so we have 4 neighbours
        var neighbour1 = new StateVector4(initialX, initialY,
            currentState.vx() + DELTA, currentState.vy());
        var neighbour2 = new StateVector4(initialX, initialY,
            currentState.vx() - DELTA, currentState.vy());


        var neighbour3 = new StateVector4(initialX, initialY,
            currentState.vx(), currentState.vy() + DELTA);
        var neighbour4 = new StateVector4(initialX, initialY,
            currentState.vx(), currentState.vy() - DELTA);

        ArrayList<StateVector4> neighbours = new ArrayList<>();
        neighbours.add(neighbour1);
        neighbours.add(neighbour2);
        neighbours.add(neighbour3);
        neighbours.add(neighbour4);

        return neighbours;
    }


    /**
     * <p>Evaluates each neighbour by the cost function h.</p>
     *
     * The cost function is the Euclidean distance from the target at the moment the ball stops,
     * (lower is better)
     *
     * @param neighbours list of neighbours
     * @return Map of {@code (neighbour, h-value)} pairs
     */
    private Map<StateVector4, Double> evaluateNeighbours(List<StateVector4> neighbours) {
        return neighbours.parallelStream()
            .collect(Collectors.toMap(
                Function.identity(),
                this::evaluateState
            ));
    }

    /**
     * <p>Evaluates a state by the cost function h.</p>
     * <p>
     * The cost function is defined as the Euclidean distance from the target at the moment the ball stops,
     * (lower is better).
     *
     * @param state the state to evaluate
     * @return h (cost function value)
     */
    private double evaluateState(StateVector4 state) {
        double targetX = this.course.targetXcoord();
        double targetY = this.course.targetYcoord();
        var engine = new PhysicsEngine(this.course, state);
        while (!engine.isAtRest()) {
            engine.nextStep();
        }
        if (engine.reachedTarget()) {
            System.out.println("neighbour = " + state);
            throw new RuntimeException("Engine reached the target!");
        }
        StateVector4 lastVector = engine.getTrajectory().getLast();
        // get the position where the ball stopped
        double finalX = lastVector.x();
        double finalY = lastVector.y();
        // compute the cost function h
        return magnitude(finalX - targetX, finalY - targetY);
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
        do {
            vector[0] = random.nextDouble() * 5 * 2 - 5; // random number between -5 and 5
            vector[1] = random.nextDouble() * 5 * 2 - 5; // random number between -5 and 5
        } while (magnitude(vector) > 5);
        return vector;

    }

}


