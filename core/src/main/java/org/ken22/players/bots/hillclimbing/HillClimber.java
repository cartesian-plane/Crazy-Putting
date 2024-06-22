package org.ken22.players.bots.hillclimbing;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.Player;
import org.ken22.players.error.ErrorFunction;
import org.ken22.players.error.EuclideanError;

import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>This class contains a simple steepest-descent hill-climbing, for achieving a hole-in one.</p>
 *
 * <p>The state space is discretized, and the best neighbours are followed.</p>
 * <p>To avoid getting stuck in a plateau, a certain amount of sideways moves are allowed.</p>
 * <p>A maximum number of random restarts can be specified, meaning the search will eventually reach a solution,
 * given enough random restarts.</p>
 * <p>If the number of random restarts is set to 0, no random restarts will be performed.</p>
 * <p>If the maximum amount of sideways moves is reached, the search restarts and, if the max number of restarts
 * has been reached, the best solution is returned.</p>
 *
 *
 * <p><b>Note: </b></p>
 * <ul>
 *     Reference: <i>Artificial Intelligence: A Modern Approach 3rd ed. (Chapter 4)</i>
 * </ul>
 */
@SuppressWarnings({"unused", "DuplicatedCode"})
public class HillClimber implements Player {

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
    private final int MAX_RESTARTS;
    private final GolfCourse course;
    private final StateVector4 initialState;
    private final ErrorFunction heuristicFunction;
    private final Evaluator evaluator;

    public HillClimber(GolfCourse course) {
        this.course = course;
        this.DELTA = 0.01;
        this.THRESHOLD = course.targetRadius();
        this.MAX_SIDEWAYS_MOVES = 10;
        this.MAX_RESTARTS = 10;
        var errorFunction = new EuclideanError();
        errorFunction.init(this.course);
        this.heuristicFunction = errorFunction;
        this.evaluator = new Evaluator(this.heuristicFunction, this.course);
        var initialX = course.ballX();
        var initialY = course.ballY();

        // choose random speed vector to start with
        var speedVector = getRandomSpeedVector();
        initialState = new StateVector4(initialX, initialY, speedVector[0], speedVector[1]);
    }

    public HillClimber(GolfCourse course, int maxRestarts, int maxSidewaysMoves) {
        this.course = course;
        this.DELTA = 0.01;
        this.THRESHOLD = course.targetRadius();
        this.MAX_SIDEWAYS_MOVES = maxSidewaysMoves;
        this.MAX_RESTARTS = maxRestarts;
        this.heuristicFunction = new EuclideanError();
        this.heuristicFunction.init(this.course);
        this.evaluator = new Evaluator(this.heuristicFunction, this.course);
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
        this.MAX_RESTARTS = 10;
        this.initialState = initialState;
        // default heuristic function
        this.heuristicFunction = new EuclideanError();
        this.evaluator = new Evaluator(this.heuristicFunction, this.course);
        this.heuristicFunction.init(this.course);
    }

    public HillClimber(GolfCourse course, StateVector4 initialState, ErrorFunction heuristicFunction) {
        this.course = course;
        this.DELTA = 0.05;
        this.THRESHOLD = course.targetRadius();
        this.MAX_SIDEWAYS_MOVES = 10;
        this.MAX_RESTARTS = 10;
        this.initialState = initialState;
        // default heuristic function
        this.heuristicFunction = heuristicFunction;
        this.heuristicFunction.init(this.course);
        this.evaluator = new Evaluator(this.heuristicFunction, this.course);
    }

    @Override
    public StateVector4 play(StateVector4 state) {
        return search(state);
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
    private StateVector4 search(StateVector4 state) {

        // flag that stores whether a solution was found
        // if the search stops before a solution is found, a logging message is displayed
        boolean foundSolution = false;

        var currentState = initialState;

        int restartCount = 0;

        while (restartCount <= MAX_RESTARTS) {

            int sidewaysMoves = 0;
            System.out.println("Restart count: " + restartCount);

            while (sidewaysMoves < MAX_SIDEWAYS_MOVES) {
                System.out.println("Sideways move: " + sidewaysMoves);

                double currentStateValue = evaluator.evaluateState(currentState);
                System.out.println("Current state value: " + currentStateValue);

                var neighbours = generateNeighbours(currentState);
//                for(StateVector4 neighbour : neighbours) {
//                    System.out.println(neighbour);
//                }
                var neighbourEvaluations = evaluator.evaluateNeighbours(neighbours);
                var bestNeighbour = Collections.min(neighbourEvaluations.entrySet(), Map.Entry.comparingByValue()).getKey();

                double bestNeighbourValue = neighbourEvaluations.get(bestNeighbour);
                System.out.println("Best neighbour value: " + bestNeighbourValue);

                if (bestNeighbourValue <= currentStateValue) {
                    sidewaysMoves = 0;
                    System.out.println("Improvement? true");
                } else {
                    sidewaysMoves += 1;
                    System.out.println("Improvement? false");
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
                break;
            } else {
                restartCount += 1;
                double[] randomSpeedVector = getRandomSpeedVector();

                // choose a new random starting point
                currentState = new StateVector4(initialState.x(), initialState.y(), randomSpeedVector[0],
                    randomSpeedVector[1]);
            }
        }

        return currentState;
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
    private StateVector4 search() {

        // flag that stores whether a solution was found
        // if the search stops before a solution is found, a logging message is displayed
        boolean foundSolution = false;

        var currentState = initialState;

        int restartCount = 0;

        while (restartCount <= MAX_RESTARTS) {

            int sidewaysMoves = 0;
            System.out.println("Restart count: " + restartCount);

            while (sidewaysMoves < MAX_SIDEWAYS_MOVES) {
                System.out.println("Sideways move: " + sidewaysMoves);

                double currentStateValue = evaluator.evaluateState(currentState);
                System.out.println("Current state value: " + currentStateValue);

                var neighbours = generateNeighbours(currentState);
//                for(StateVector4 neighbour : neighbours) {
//                    System.out.println(neighbour);
//                }
                var neighbourEvaluations = evaluator.evaluateNeighbours(neighbours);
                var bestNeighbour = Collections.min(neighbourEvaluations.entrySet(), Map.Entry.comparingByValue()).getKey();

                double bestNeighbourValue = neighbourEvaluations.get(bestNeighbour);
                System.out.println("Best neighbour value: " + bestNeighbourValue);

                if (bestNeighbourValue <= currentStateValue) {
                    sidewaysMoves = 0;
                    System.out.println("Improvement? true");
                } else {
                    sidewaysMoves += 1;
                    System.out.println("Improvement? false");
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
                break;
            } else {
                restartCount += 1;
                double[] randomSpeedVector = getRandomSpeedVector();

                // choose a new random starting point
                currentState = new StateVector4(initialState.x(), initialState.y(), randomSpeedVector[0],
                    randomSpeedVector[1]);
            }
        }

        return currentState;
    }

    /**
     * <p>Generates a given state's neighbours by discretizing its neighborhood.</p>
     *
     * <p>The state space is discretised by changing either one, or both of the vector components
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


