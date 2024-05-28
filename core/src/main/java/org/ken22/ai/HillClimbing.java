package org.ken22.ai;

import org.ken22.input.courseinput.CourseParser;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physicsx.engine.PhysicsEngine;
import org.ken22.physicsx.utils.PhysicsUtils;
import org.ken22.physicsx.vectors.StateVector4;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HillClimbing {
    private StateVector4 initialState;
    private GolfCourse course;
    private Set<StateVector4> visitedStates;

    public static void main(String[] args) {

        var courseFile = new File("assets/input/golf-course.json");
        var parser = new CourseParser(courseFile);
        GolfCourse golfCourse = parser.getCourse();
        StateVector4 initialState = new StateVector4(-3.0, 0.0, 3, 1.5);
        var climber = new HillClimbing(golfCourse, initialState);

        climber.search();
    }
    public HillClimbing(GolfCourse course, StateVector4 initialState) {
        this.course = course;
        this.initialState = initialState;
        visitedStates = new HashSet<>();
    }

    public StateVector4 search() {
        var currentState = randomInitialState();
        visitedStates.add(currentState);
        int sidewaysMoves = 0;
        int maxSideWaysMoves = 10;

        double temperature = 10000;
        double coolingRate = 0.003;

        while (sidewaysMoves < maxSideWaysMoves) {
            //var neighbour = generateRandomNeighbour(currentState);
            System.out.println("currentState = " + currentState);
            //System.out.println("new neighbour = " + neighbour);
            //double neighbourValue = evaluateState(neighbour);
            double currentValue = evaluateState(currentState);
            List<StateVector4> neighbours = generateNeighbours(currentState);
            HashMap<StateVector4, Double> evaluations = evaluateNeighbours(neighbours);

            // get the best neighbour
            var bestNeighbour = Collections.min(evaluations.entrySet(), Map.Entry.comparingByValue()).getKey();

//
//            double currentEnergy = evaluateState(currentState);
//            double neighbourEnergy = evaluateState(neighbour);

//            if (acceptanceProbability(currentEnergy, neighbourEnergy, temperature) > Math.random()) {
//                currentState = neighbour;
//            }
//
//            temperature *= 1 - coolingRate;

            if (evaluations.get(bestNeighbour) < currentValue) {
                currentState = bestNeighbour;
                System.out.println("Changed current state to best neighbour ");
                visitedStates.add(currentState);
                sidewaysMoves = 0;  // reset the count of sideways moves
            } else {
                System.out.println("Making sideway move: count("+sidewaysMoves+"/10)");
                sidewaysMoves++;
                currentState = generateRandomNeighbour(currentState);
                visitedStates.add(currentState);
                if (sidewaysMoves == maxSideWaysMoves) {
                    System.out.println("restarting...");
                    currentState = randomInitialState();
                    sidewaysMoves = 0;
                }
            }


            System.out.println("BEst h so far: " + currentValue);
        }

        return currentState;
    }
    /**
     * The size of the step taken when exploring (generating neighbours).
     * A reasonable value has to be chosen; if it is too small, there will hardly be a difference
     * between the neighbouring states.
     * Because the search is performed in a 2D-space (vx, vy) the "step" can be thought of as the Euclidean distance.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final double STEP_SIZE = 0.05;

    /**
     * Generates the neighbours of the current state.
     * Each generated neighbour (n) will be {@code STEP_SIZE} away from the current state (c).
     * The distance is computed as ||c - n||<sub>2</sub>.
     * @param currentState the current state
     * @return list of all possible neighbours such that ||c<sub>i</sub> - n||<sub>2</sub> = {@code STEP_SIZE},
     * for all i.
     */
    private ArrayList<StateVector4> generateNeighbours(StateVector4 currentState) {

        ArrayList<StateVector4> neighbours = new ArrayList<>();
        // always keep the same initial position, since it has to score a hole-in-one
        final double initialX = currentState.x();
        final double initialY = currentState.y();

        // generate all the possible neighbours
        // either increase vx, vy equally, or take a full step in one component only
        var neighbour1 = new StateVector4(initialX, initialY,
            currentState.vx() + STEP_SIZE/Math.sqrt(2), currentState.vy() + STEP_SIZE/Math.sqrt(2));
        var neighbour2 = new StateVector4(initialX, initialY,
            currentState.vx() + STEP_SIZE, currentState.vy());
        var neighbour3 = new StateVector4(initialX, initialY,
            currentState.vx(), currentState.vy() + STEP_SIZE);
        var neighbour4 = new StateVector4(initialX, initialY,
            currentState.vx() - STEP_SIZE/Math.sqrt(2), currentState.vy() - STEP_SIZE/Math.sqrt(2));
        var neighbour5 = new StateVector4(initialX, initialY,
            currentState.vx() - STEP_SIZE, currentState.vy());
        var neighbour6 = new StateVector4(initialX, initialY,
            currentState.vx(), currentState.vy() - STEP_SIZE);

        if (!visitedStates.contains(neighbour1)) neighbours.add(neighbour1);
        if (!visitedStates.contains(neighbour2)) neighbours.add(neighbour2);
        if (!visitedStates.contains(neighbour3)) neighbours.add(neighbour3);
        if (!visitedStates.contains(neighbour4)) neighbours.add(neighbour4);
        if (!visitedStates.contains(neighbour5)) neighbours.add(neighbour5);
        if (!visitedStates.contains(neighbour6)) neighbours.add(neighbour6);

        return neighbours;
    }

    private StateVector4 generateRandomNeighbour(StateVector4 currentState) {
        double randomVx = 10;
        double randomVy = 10;

        while (PhysicsUtils.magnitude(randomVx, randomVy) > this.course.maximumSpeed()) {
            double randomChangeX = 2 * STEP_SIZE * Math.random() - STEP_SIZE;
            double randomChangeY = 2 * STEP_SIZE * Math.random() - STEP_SIZE;

            System.out.println("randomChangeX = " + randomChangeX);
            System.out.println("randomChangeY = " + randomChangeY);
            randomVx = currentState.vx() + randomChangeX;
            randomVy = currentState.vy() + randomChangeY;
        }

        return new StateVector4(currentState.x(), currentState.y(), randomVx, randomVy);
    }

    /**
     * Evaluates each neighbour by the cost function h.
     * The cost function is the Euclidean distance from the target at the moment the ball stops,
     * given that initial state (lower is better)
     * @param neighbours list of neighbours
     * @return map of {@code (neighbour, h-value)} pairs
     */
    private HashMap<StateVector4, Double> evaluateNeighbours(List<StateVector4> neighbours) {
        double targetX = this.course.targetXcoord();
        double targetY = this.course.targetYcoord();

        HashMap<StateVector4, Double> neighbourValues = new HashMap<>(neighbours.size());

        neighbourValues = (HashMap<StateVector4, Double>) neighbours.parallelStream()
            .collect(Collectors.toMap(
                Function.identity(),  // The neighbour itself as the key
                this::evaluateState   // The evaluation of the neighbour as the value
            ));

        return neighbourValues;
    }

    /**
     * Evaluates a state by the cost function h.
     * The cost function is defined as the Euclidean distance from the target at the moment the ball stops,
     * given that initial state (lower is better).
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
        double h = PhysicsUtils.magnitude(finalX - targetX, finalY - targetY);
        return h;
    }

    /**
     * Generates a new, random initial state for restarting the search.
     * @return random initial state
     */
    private StateVector4 randomInitialState() {
        double vx = 10;
        double vy = 10;
        while (PhysicsUtils.magnitude(vx, vy) > this.course.maximumSpeed()) {
            vx = Math.random() * 4.9;
            vy = Math.random() * 4.9;  // random number between 0 and 5
        }

        return new StateVector4(course.ballX(), course.ballY(), vx, vy);

    }


    private double acceptanceProbability(double currentEnergy, double neighbourEnergy, double t) {
        if (neighbourEnergy < currentEnergy) {
            return 1.0;
        }
        return Math.exp((currentEnergy - neighbourEnergy) / t);
    }
}
