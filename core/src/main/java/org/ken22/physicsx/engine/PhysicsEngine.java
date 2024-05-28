package org.ken22.physicsx.engine;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physicsx.differentiators.Differentiator;
import org.ken22.physicsx.differentiators.FivePointCenteredDifference;
import org.ken22.physicsx.differentiation.VectorDifferentiation4;
import org.ken22.physicsx.differentiation.VectorDifferentiationFactory;
import org.ken22.physicsx.odesolvers.ODESolver;
import org.ken22.physicsx.odesolvers.RK4;
import org.ken22.physicsx.utils.PhysicsUtils;
import org.ken22.physicsx.vectors.StateVector4;

import java.util.ArrayList;
import java.util.Iterator;

public class PhysicsEngine {

    private static final double DEFAULT_TIME_STEP = 0.0001;
    private static final double STOPPING_THRESHOLD = 0.05;
    private final GolfCourse course;
    private final Expression expr;

    private final VectorDifferentiationFactory vectorDifferentiationFactory;
    private final ODESolver<StateVector4> solver;
    private final Differentiator differentiator;

    private ArrayList<StateVector4> trajectory = new ArrayList<>();
    private StateVector4 initialStateVector;
    private final double timeStep; // Default time step


    /// Constructors
    public PhysicsEngine(GolfCourse course, StateVector4 initialStateVector) {
        this(course, initialStateVector, DEFAULT_TIME_STEP, new FivePointCenteredDifference(), new RK4());
    }

    public PhysicsEngine(GolfCourse course, StateVector4 initialStateVector, double timeStep) {
        this(course, initialStateVector, timeStep, new FivePointCenteredDifference(), new RK4());
    }

    public PhysicsEngine(GolfCourse course, StateVector4 initialStateVector, double timeStep,
                         Differentiator differentiator, ODESolver<StateVector4> solver) {

        if (timeStep > 0.016) {
            throw new IllegalArgumentException("Step size " + timeStep + " too big for 60FPS");
        }

        double vx = initialStateVector.vx();
        double vy = initialStateVector.vy();
        if (PhysicsUtils.magnitude(vx, vy) > course.maximumSpeed()) {
            throw new IllegalArgumentException("Initial vector speed too high! (max speed = "
                + course.maximumSpeed() + ")");
        }

        this.course = course;
        this.initialStateVector = initialStateVector;
        this.timeStep = timeStep;
        this.differentiator = differentiator;
        this.solver = solver;
        this.expr = new ExpressionBuilder(course.courseProfile())
            .variables("x", "y")
            .build();
        this.vectorDifferentiationFactory = new VectorDifferentiationFactory(timeStep, expr, course, differentiator);
        trajectory.add(initialStateVector);
    }


    /// Methods

    /**
     * Checks whether the ball is at rest by looking at the last computed state vector.
     *
     * <p>The ball is considered to be at rest if any of these conditions are true:</p>
     * <ul>
     *     <li>has collided (i.e. went into water)</li>
     *     <li>has gone out of the terrain bounds</li>
     *     <li>the speed is small and the slope is negligible</li>
     *     <li>the static friction overcomes the downhill force</li>
     * </ul>
     *
     * @return {@code true} if at rest, {@code false} otherwise
     */
    @SuppressWarnings("RedundantIfStatement")
    public boolean isAtRest() {
        StateVector4 lastVector = trajectory.getLast();

        if (underwater()) {
            //System.out.println("Underwater");
            return true;
        } else if(outOfBounds()) {
            //System.out.println("Out of bounds");
            return true;
        } else if (reachedTarget()) {
            //System.out.println("Reached target");
            return true;
        }

        double x = lastVector.x();
        double y = lastVector.y();
        double vx = lastVector.vx();
        double vy = lastVector.vy();

        // Evaluate the partial derivatives for x and y
        double dh_dx = PhysicsUtils.xSlope(x, y, timeStep, expr, differentiator);
        double dh_dy = PhysicsUtils.ySlope(x, y, timeStep, expr, differentiator);

        if (PhysicsUtils.magnitude(vx, vy) < STOPPING_THRESHOLD) {
            if (PhysicsUtils.magnitude(dh_dx, dh_dy) < STOPPING_THRESHOLD) {
                return true;
            } else if (course.staticFrictionGrass() > PhysicsUtils.magnitude(dh_dx, dh_dy)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Check if the ball has collided (went into water).
     *
     * @return {@code true} if the height is negative, {@code false} otherwise.
     */
    public boolean underwater() {
        StateVector4 lastVector = trajectory.getLast();

        double x = lastVector.x();
        double y = lastVector.y();

        expr.setVariable("x", x)
            .setVariable("y", y);

        double height = expr.evaluate();

        return height < 0;
    }

    /**
     * Checks if the ball has reached the target.
     *
     * @return {@code true} if reached, {@code false} otherwise
     */
    public boolean reachedTarget() {
        StateVector4 lastVector = trajectory.getLast();

        double x = lastVector.x();
        double y = lastVector.y();

        double targetRadius = course.targetRadius();
        double targetX = course.targetXcoord();
        double targetY = course.targetYcoord();

        return PhysicsUtils.magnitude(x - targetX, y - targetY) < targetRadius;
    }

    /**
     * Check if the ball has gone out of bounds.
     * @return TRUE if the ball is out of bounds, FALSE otherwise
     */
    public boolean outOfBounds() {
        return Math.abs(trajectory.getLast().x()) > course.range() || Math.abs(trajectory.getLast().y()) > course.range();
    }

    /**
     * Generates, appends and returns the next state vector in the trajectory according to the step size
     * Does not check whether the ball is at rest
     *
     * @return {@link StateVector4} the next state vector
     */
    @SuppressWarnings("UnusedReturnValue")
    public StateVector4 nextStep() {
        StateVector4 lastVector = trajectory.getLast();
        double vx = lastVector.vx();
        double vy = lastVector.vy();

        VectorDifferentiation4 differentiation;
        // Decide which equations to use for updating the acceleration
        if (PhysicsUtils.magnitude(vx, vy) < STOPPING_THRESHOLD) {
            differentiation = vectorDifferentiationFactory.lowSpeedVectorDifferentiation4();
        } else {
            differentiation = vectorDifferentiationFactory.normalSpeedVectorDifferentiation4();
        }

        StateVector4 newVector = solver.nextStep(timeStep, lastVector, differentiation);
        trajectory.add(newVector);
        return newVector;
    }

    /**
     * Returns the final prediction of the trajectory of the golf ball
     * Speeds up the rest of the simulation to get the final prediction, causing problems to existing iterators
     * <p>
     * The method will keep calling {@link #nextStep()} until the ball is at rest
     *
     * @return {@link StateVector4} the final prediction
     */
    public StateVector4 solve() {
        while (!isAtRest()) {
            nextStep();
        }
        return trajectory.getLast();
    }


    public FrameRateIterator iterator() {
        return new FrameRateIterator();
    }

    /**
     * Returns an iterator that will iterate over the trajectory of the golf ball for graphical display
     * <p>
     * Assumes that time matches dt = 1 -> 1s has passed, and frame rate of 60FPS
     */
    public class FrameRateIterator implements Iterator<StateVector4> {
        private static final int FRAME_RATE = 60;
        private final int kPerFrame = (int) ((1.0 / FRAME_RATE) / timeStep);
        private int index = 0; //Iterator must keep reference to the current element/index

        @Override
        public boolean hasNext() {
            return !isAtRest();
        }

        @Override
        public StateVector4 next() {
            for (int i = 0; i < kPerFrame; i++) {
                nextStep();
                if (isAtRest()) {
                    break;
                }
            }
            return trajectory.getLast();
        }
    }

    public ArrayList<StateVector4> getTrajectory() {
        return trajectory;
    }

    public class StepIterator implements Iterator<StateVector4> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < trajectory.size() || !isAtRest();
        }

        @Override
        public StateVector4 next() {
            if (index >= trajectory.size()) {
                return nextStep();
            }
            return trajectory.get(index++);
        }
    }

    public StepIterator stepIterator() {
        return new StepIterator();
    }
}
