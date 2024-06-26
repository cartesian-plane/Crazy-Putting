package org.ken22.players.bots.hillclimbing;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.input.settings.GeneralSettings;
import org.ken22.physics.PhysicsFactory;
import org.ken22.physics.differentiators.Differentiator;
import org.ken22.physics.differentiators.FivePointCenteredDifference;
import org.ken22.physics.odesolvers.outofplace.ODESolver;
import org.ken22.physics.odesolvers.outofplace.RK4;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.Player;
import org.ken22.players.bots.simulatedannealing.Evaluator;
import org.ken22.players.error.ErrorFunction;
import org.ken22.players.error.GradientDescent2;

import java.util.*;
import java.util.function.Function;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * <p>This class contains a simple steepest-descent hill-climbing.</p>
 *
 * <p>The state space is discretized, and the best neighbours are followed.</p>
 * <p>To avoid getting stuck in a plateau, a certain amount of sideways moves are allowed.</p>
 * <p>Up to 400 visited states can be stored to avoid cycles </p>
 * <p>A maximum number of random restarts can be specified, meaning the search will eventually reach a solution,
 * given enough random restarts.</p>
 * <p>If the number of random restarts is set to 0, no random restarts will be performed.</p>
 * <p>If the maximum amount of sideways moves is reached, the search restarts and, if the max number of restarts
 * has been reached, the best solution is returned.</p>
 *
 *
 *
 *
 * <p><b>Note: </b></p>
 * <ul>
 *     Reference: <i>Artificial Intelligence: A Modern Approach 3rd ed. (Chapter 4)</i>
 * </ul>
 */
@SuppressWarnings({"unused", "DuplicatedCode"})
public class SidewaysStepsHillCrimbingBot implements Player {

    private static final Logger LOGGER = Logger.getLogger(SidewaysStepsHillCrimbingBot.class.getName());

    static {

        // the default level is INFO
        // if you want to change logging, just change the enum type at (1) and (2)
        // https://docs.oracle.com/javase/8/docs/api/java/util/logging/Level.html
        LOGGER.setLevel(Level.FINER); // (1)


        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE); // (2)
        LOGGER.addHandler(consoleHandler);
    }

    public  double getDELTA() {
        return this.DELTA;
    }

    private final GolfCourse course;
    private final double DELTA;
    private final double THRESHOLD;
    private final int MAX_SIDEWAYS_MOVES;
    private final int MAX_RESTARTS;
    private final ODESolver<StateVector4> solver;
    private final Differentiator differentiator;
    private final double stepSize;
    private final ErrorFunction heuristicFunction;
    private final Evaluator evaluator;
    private GeneralSettings settings;

    public SidewaysStepsHillCrimbingBot(GolfCourse course, PhysicsFactory factory) {
        this(0.01, course.targetRadius(),  10, 10, course, new RK4(),
            new FivePointCenteredDifference(), 0.0001, new GradientDescent2(), factory);
    }

    public SidewaysStepsHillCrimbingBot(GolfCourse course, PhysicsFactory factory, ErrorFunction errorFunction) {
        this(0.01, course.targetRadius(),  10, 10, course, new RK4(),
            new FivePointCenteredDifference(), 0.0001, errorFunction, factory);
    }

    public SidewaysStepsHillCrimbingBot(GolfCourse course, PhysicsFactory factory, int maxRestarts, int maxSidewaysMoves) {
        this(0.01, course.targetRadius(),  maxSidewaysMoves, maxRestarts, course, new RK4(),
            new FivePointCenteredDifference(), 0.0001, new GradientDescent2(), factory);
    }

    public SidewaysStepsHillCrimbingBot(double DELTA, double THRESHOLD, int MAX_SIDEWAYS_MOVES, int MAX_RESTARTS, GolfCourse course,
                                        ODESolver<StateVector4> solver, Differentiator differentiator, double stepSize,
                                        ErrorFunction heuristicFunction, PhysicsFactory physicsFactory) {
        this.DELTA = DELTA;
        this.THRESHOLD = THRESHOLD;
        this.MAX_SIDEWAYS_MOVES = MAX_SIDEWAYS_MOVES;
        this.MAX_RESTARTS = MAX_RESTARTS;
        this.course = course;
        this.solver = solver;
        this.differentiator = differentiator;
        this.stepSize = stepSize;
        this.heuristicFunction = heuristicFunction;
        this.heuristicFunction.init(course, physicsFactory);
        this.evaluator = new Evaluator(this.heuristicFunction, this.course, this.solver, this.differentiator,
            this.stepSize);
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
    private StateVector4 search(StateVector4 initialState) {

        // flag that stores whether a solution was found
        // if the search stops before a solution is found, a logging message is displayed
        boolean foundSolution = false;

        //Store visited states
        int visitedidx = 0;
        StateVector4[] visited = new StateVector4[400];

        //Initialise current state
        var currentState = initialState;
        var currentStateValue = evaluator.evaluateState(initialState);

        //Add initial state to neighborus
        visited[visitedidx] = currentState;
        visitedidx++;

        //Keep track of restarts
        int restartCount = 0;

        while (restartCount <= MAX_RESTARTS) {

            //Keep track of sideways moves
            int sidewaysMoves = 0;

            //Reference point (local minimum) from when you started making sideways moves
            var referenceStatePoint = currentState;
            var referenceStateValue = evaluator.evaluateState(referenceStatePoint);

            //Direction of sideways move
            Direction currentDirection = null; //private inner enum


            while (sidewaysMoves < MAX_SIDEWAYS_MOVES) {

                var neighbours = generateNeighbours(currentState, visited, visitedidx);
                if (neighbours.isEmpty()) {
                    break;
                }

                var neighbourEvaluations = evaluator.evaluateNeighbours(neighbours);
                var bestNeighbour = Collections.min(neighbourEvaluations.entrySet(), Map.Entry.comparingByValue()).getKey();
                double bestNeighbourValue = neighbourEvaluations.get(bestNeighbour);

                if(sidewaysMoves > 0) { // previous move was a sideways move (escaping local minimum)
                    if(bestNeighbourValue <= referenceStateValue) { //escaped local minimum
                        currentState = bestNeighbour;
                        currentStateValue = bestNeighbourValue;
                        referenceStateValue = bestNeighbourValue; //keep track of local minimum value to be escaped
                        sidewaysMoves = 0;
                    }
                    else { //not escaped local minimum
                        //check direction from which local minimum was approached
                        currentDirection = checkDirection(bestNeighbour, currentState);
                        //move along direction of approach
                        currentState = moveSideways(currentState, currentDirection);
                        sidewaysMoves++;
                    }
                }

                else { //  previous move was not a sideways move (not escaping local minimum)
                    if (bestNeighbourValue >= currentStateValue) { // local minimum
                        currentDirection = checkDirection(bestNeighbour, currentState);
                        currentState = moveSideways(currentState, currentDirection);;
                        sidewaysMoves++;
                    }
                    else { // not local minimum
                        referenceStateValue = bestNeighbourValue;
                        currentState = bestNeighbour;
                        currentStateValue = bestNeighbourValue;
                    }
                }

                //Update number of visited states
                visited[visitedidx] = currentState;
                visitedidx++;

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
            } else { //If solution not found start using new random initial guess
                restartCount += 1;
                double[] randomSpeedVector = getRandomSpeedVector();
                // choose a new random starting point
                currentState = new StateVector4(initialState.x(), initialState.y(), randomSpeedVector[0],
                    randomSpeedVector[1]);
                visited = new StateVector4[400];;
                visitedidx = 0;
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
    public StateVector4 search2(StateVector4 initialState) {

        // flag that stores whether a solution was found
        // if the search stops before a solution is found, a logging message is displayed
        boolean foundSolution = false;

        int visitedidx = 0;
        StateVector4[] visited = new StateVector4[100];

        var currentState = initialState;
        visited[visitedidx] = currentState;
        var previousState = currentState;

        int restartCount = 0;

        while (restartCount <= MAX_RESTARTS) {

            int sidewaysMoves = 0;

            while (sidewaysMoves < MAX_SIDEWAYS_MOVES) {

                double currentStateValue = evaluator.evaluateState(currentState);

                var neighbours = generateNeighbours(currentState, visited, visitedidx);
//                for(StateVector4 neighbour : neighbours) {
//                    System.out.println(neighbour);
//                }
                neighbours.remove(previousState);

                var neighbourEvaluations = evaluator.evaluateNeighbours(neighbours);
                var bestNeighbour = Collections.min(neighbourEvaluations.entrySet(), Map.Entry.comparingByValue()).getKey();

                double bestNeighbourValue = neighbourEvaluations.get(bestNeighbour);

                if (bestNeighbourValue <= currentStateValue) {
                    sidewaysMoves = 0;
                } else {
                    sidewaysMoves += 1;
                }

                previousState = currentState;
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
                visited = new StateVector4[400];
                visitedidx = 0;

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
    private List<StateVector4> generateNeighbours(StateVector4 currentState, StateVector4[] visited, int visitedidx) {

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

        if (neighbour1.vx() * neighbour1.vx() + neighbour1.vy() * neighbour1.vy() < 25) {
            neighbours.add(neighbour1);
        }
        if (neighbour2.vx() * neighbour2.vx() + neighbour2.vy() * neighbour2.vy() < 25) {
            neighbours.add(neighbour2);
        }
        if (neighbour3.vx() * neighbour3.vx() + neighbour3.vy() * neighbour3.vy() < 25) {
            neighbours.add(neighbour3);
        }
        if (neighbour4.vx() * neighbour4.vx() + neighbour4.vy() * neighbour4.vy() < 25) {
            neighbours.add(neighbour4);
        }

        removeVisited(visited, neighbours, visitedidx);

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
        return evaluator.evaluateState(state);
    }

    /**
     * <p> Generate a random speed vector.</p>
     * Ensures that the magnitude of the vector does not exceed 5m/s
     *
     * @return randomly generated speed vector (vx, vy)
     */
    private double[] getRandomSpeedVector(StateVector4[] visited) {
        boolean similar = true;
        double[] vector = new double[2];

        while(similar) {
            Random random = new Random();
            vector[0] = random.nextDouble() * 5 * 2 - 5; // random number between -5 and 5
            double x = Math.sqrt(this.course.maximumSpeed()-vector[0]*vector[0]);
            vector[1] = random.nextDouble()*2*x-x; // random number between -5 and 5

            for(StateVector4 state : visited) {
                if(state.sameVelocity(vector, 0.01)) {
                    similar = true;
                    break;
                }
                else {
                    similar = false;
                }
            }
        }
        return vector;
    }

    /**
     * <p> Generate random vx and vy velocities </p>
     * @return: vector containing vx and vy velocities
     */
    private double[] getRandomSpeedVector() {
        double[] vector = new double[2];
        Random random = new Random();
        vector[0] = random.nextDouble() * 5 * 2 - 5; // random number between -5 and 5
        double x = Math.sqrt(this.course.maximumSpeed()-vector[0]*vector[0]);
        vector[1] = random.nextDouble()*2*x-x; // random number between -5 and 5
        return vector;
    }

    /**
     * <p> Check if any of the current neighbours have already been visited by the ball </p>
     * @param visited: array of previous visited states
     * @param neighbours: array of generated neighbours
     * @param visitedidx: how many visited states are currently stored
     */
    private void removeVisited(StateVector4[] visited, List<StateVector4> neighbours, int visitedidx) {
        List<StateVector4> toRemove = new ArrayList<>();

        for (int i = 0; i < visitedidx; i++) {
            if (visited[i] == null) {
                throw new RuntimeException("Previous states are not being stored correctly.");
            }

            for (StateVector4 neighbour : neighbours) {
                if (visited[i].approxEquals(neighbour, course.targetRadius(), this.DELTA)) {
                    toRemove.add(neighbour);
                }
            }
        }
        neighbours.removeAll(toRemove);
    }

    /**
     * <p> Check which direction speed changed from last position </p>
     * @param bestNeighbour: current best neighbour
     * @param currentState: current state
     * @return: direction of motion in the vx, vy phase space
     */
    private Direction checkDirection(StateVector4 bestNeighbour, StateVector4 currentState) {
        if(bestNeighbour.vx() > currentState.vx()) {
            return Direction.positiveX;
        }
        else if(bestNeighbour.vx() < currentState.vx()) {
            return Direction.negativeX;
        }
        else if(bestNeighbour.vy() > currentState.vy()) {
            return Direction.positiveY;
        }
        else {
            return Direction.negativeY;
        }
    }

    /**
     * <p> Returns new state vector after sideways move </p>
     * @param currentState: currentState vector
     * @param direction: direction from which local minimum was initially approached
     * @return: a new state vector after sideways move
     */
    private StateVector4 moveSideways(StateVector4 currentState, Direction direction) {
        return switch (direction) {
            case Direction.negativeX ->
                new StateVector4(currentState.x(), currentState.y(), currentState.vx() - DELTA, currentState.vy());
            case Direction.positiveY ->
                new StateVector4(currentState.x(), currentState.y(), currentState.vx(), currentState.vy() + DELTA);
            case Direction.negativeY ->
                new StateVector4(currentState.x(), currentState.y(), currentState.vx(), currentState.vy() - DELTA);
            default ->
                new StateVector4(currentState.x(), currentState.y(), currentState.vx() + DELTA, currentState.vy());
        };
    }

    /**
     * <p> Enumerates possible directions of motion in phase space </p>
     */
    private enum Direction {
        positiveX, negativeX, positiveY, negativeY
    }
}


