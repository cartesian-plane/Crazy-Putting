package org.ken22.players.bots.simulatedannealing;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.differentiators.Differentiator;
import org.ken22.physics.differentiators.FivePointCenteredDifference;
import org.ken22.physics.odesolvers.outofplace.ODESolver;
import org.ken22.physics.odesolvers.outofplace.RK4;
import org.ken22.physics.utils.PhysicsUtils;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.Player;
import org.ken22.players.bots.simplebots.InitialGuessBot;
import org.ken22.players.error.ErrorFunction;
import org.ken22.players.error.EuclideanError;
import org.ken22.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;
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
    private static final Logger LOGGER = Logger.getLogger(SimulatedAnnealing.class.getName());

    static {

        // the default level is INFO
        // if you want to change logging, just change the enum type at (1) and (2)
        // https://docs.oracle.com/javase/8/docs/api/java/util/logging/Level.html
        LOGGER.setLevel(Level.INFO); // (1)


        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO); // (2)
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(consoleHandler);
    }

    private static final int NR_BOOTSTRAP_VECTORS = 15;
    private final double DELTA;
    private final double THRESHOLD;
    private final double initialTemperature;
    private final Schedule schedule;
    private final GolfCourse course;
    private double ballX;
    private double bally;
    private final ODESolver<StateVector4> solver;
    private final Differentiator differentiator;
    private final double stepSize;
    private double allottedTime;
    private int kmax = 1000;
    private Player initialGuess;

    private final ErrorFunction heuristicFunction;
    private final Evaluator evaluator;

    public SimulatedAnnealing(GolfCourse course) {
        this.course = course;
        this.ballX = course.ballX;
        this.bally = course.ballY;
        this.solver = new RK4();
        this.differentiator = new FivePointCenteredDifference();
        this.stepSize = 0.0001;
        this.DELTA = 0.01;
        this.THRESHOLD = course.targetRadius;
        this.heuristicFunction = new EuclideanError();
        // this.heuristicFunction.init(this.course);
        this.evaluator = new Evaluator(this.heuristicFunction, this.course);
        this.initialTemperature = 100;
        this.schedule = new LinearCooling(initialTemperature, 0.8);
        this.initialGuess = new InitialGuessBot(course);

        var initialX = course.ballX();
        var initialY = course.ballY();

        // choose random speed vector to start with
        var speedVector = getRandomSpeedVector();
        // initialState = new StateVector4(initialX, initialY, speedVector[0], speedVector[1]);
    }

    public SimulatedAnnealing(Player initialGuess, GolfCourse course,
                                ODESolver<StateVector4> solver,
                                Differentiator differentiator,
                                double stepSize,
                                double initialTemperature,
                                double allottedTime,
                                ErrorFunction errorFunction) {
        LOGGER.log(Level.FINE, "Initializing simulated annealing");
        this.initialGuess = initialGuess;
        this.course = course;
        this.ballX = course.ballX;
        this.bally = course.ballY;
        this.solver = solver;
        this.differentiator = differentiator;
        this.stepSize = stepSize;
        this.DELTA = 0.05;
        this.THRESHOLD = course.targetRadius;
        this.heuristicFunction = errorFunction;
        LOGGER.log(Level.INFO, "Error function for simulated annealing: " + heuristicFunction.getClass().getName());
        LOGGER.log(Level.INFO, "THRESHOLD = " + THRESHOLD);
        this.evaluator = new Evaluator(this.heuristicFunction, this.course, this.solver, this.differentiator,
            this.stepSize);

        this.initialTemperature = 1000;
        this.allottedTime = allottedTime;
        this.schedule = new LinearCooling(initialTemperature, 0.5);
    }

    public StateVector4 play(StateVector4 state) {
        // update the ball's position, so the bot is aware of the new position (if applicable)
        ballX = state.x;
        bally = state.y;

        // flag that stores whether a solution was found
        // if the search stops before a solution is found, a logging message is displayed
        boolean foundSolution = false;
        double temperature;
        StateVector4 bestState;
        if (initialGuess != null)
            bestState = initialGuess.play(state);
        else
            bestState = bootstrap();

        var bestStateValue = evaluator.evaluateState(bestState);
        StateVector4 current = bestState;
        // early return if we stumble upon the solution
        if (evaluator.evaluateState(bestState) < THRESHOLD) {
            return bestState;
        }
        for (int k = 0; k < kmax; k++) {
            temperature = schedule.getNewTemperature(k);
            LOGGER.log(Level.FINE, "Temperature = " + temperature);
            var next = getRandomNeighbour(bestState);
            double nextValue = evaluator.evaluateState(next);
            double currentValue = evaluator.evaluateState(current);

            // check if a solution is reached
            if (nextValue < THRESHOLD) {
                bestState = next;
                foundSolution = true;
                break;
            }
            var deltaE = currentValue - nextValue;

            if (deltaE >= 0) {
                current = next; // if the evaluation is improved, accept immediately
            } else {
                double probability = Math.exp(deltaE / temperature);
                LOGGER.log(Level.FINE, "probability = " + probability);
                LOGGER.log(Level.FINE, "deltaE = " + deltaE);
                if (Math.random() < probability) {
                    current = next;
                }
            }

            if (nextValue < bestStateValue) {
                bestStateValue = nextValue;
                bestState = current;
            }
        }

        String message;
        if (foundSolution) {
            message = "Found solution! " + "vx: " + bestState.vx() + ", vy: " + bestState.vy();
        } else {
            message = "Solution not found but allotted time ran out";
        }
        LOGGER.log(Level.INFO, message);

        System.out.println("RETURNING current = " + bestState);
        return bestState;
    }

    private StateVector4 getRandomNeighbour(StateVector4 currentState) {

        final double initialVx = currentState.vx();
        final double initialVy = currentState.vy();

        // uniform distribution on a circle
        var radius = DELTA;
        var angle = Math.random() * 2 * Math.PI;
        double vx = initialVx + radius * Math.cos(angle);
        double vy = initialVy + radius * Math.sin(angle);

        return new StateVector4(currentState.x(), currentState.y(), vx, vy);
    }



    /**
     * <p>Bootstrap the search by generating a list of random vectors and selecting the best one.</p>
     *
     * @return the best vector from the list of random vectors
     */
    private StateVector4 bootstrap() {
        LOGGER.log(Level.INFO, "Bootstrapping the search (might take a while");
        var randomVectors = getRandomVectors();
        StateVector4 bestVector = null;
        double bestVectorValue = Double.MAX_VALUE;

        for (var vector : randomVectors) {
            double vectorValue = evaluator.evaluateState(vector);
            // check if we're lucky and the solution was generated randomly
            if (vectorValue < THRESHOLD) {
                return vector;
            }
            if (vectorValue < bestVectorValue) {
                bestVectorValue = vectorValue;
                bestVector = vector;
            }
        }

        // just return a random one if the really is no better vector
        return (bestVector == null ? randomVectors.getFirst() : bestVector);
    }

    /**
     * <p>Generate a list of random vectors to bootstrap the search.</p>
     * Ensures that the magnitude of the vector does not exceed 5m/s
     *
     * @return list of random vectors
     */
    private List<StateVector4> getRandomVectors() {
        double[] vx_s = MathUtils.linspace(-5, 5, NR_BOOTSTRAP_VECTORS);
        double[] vy_s = MathUtils.linspace(-5, 5, NR_BOOTSTRAP_VECTORS);

        ArrayList<StateVector4> randomVectors = new ArrayList<>();
        for (double vx : vx_s) {
            for (double vy: vy_s) {
                if (PhysicsUtils.magnitude(vx, vy) < course.maximumSpeed) {
                    randomVectors.add(new StateVector4(ballX, bally, vx, vy));
                }
            }
        }
        return randomVectors;
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
            double x = Math.sqrt(25-vector[0]*vector[0]);
            vector[1] = random.nextDouble()*2*x-x; // random number between -5 and 5

        } while (PhysicsUtils.magnitude(vector[0], vector[1]) > course.maximumSpeed);
        return vector;
    }
}
