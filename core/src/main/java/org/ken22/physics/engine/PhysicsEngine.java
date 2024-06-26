package org.ken22.physics.engine;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.obstacles.Tree;
import org.ken22.obstacles.Wall;
import org.ken22.physics.differentiators.Differentiator;
import org.ken22.physics.differentiators.FivePointCenteredDifference;
import org.ken22.physics.differentiation.outofplace.VectorDifferentiation4;
import org.ken22.physics.differentiation.outofplace.VectorDifferentiationFactory;
import org.ken22.physics.odesolvers.outofplace.ODESolver;
import org.ken22.physics.odesolvers.outofplace.RK4;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.utils.MathUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class PhysicsEngine {

    private static final double BOUNCE_CONSTANT = 0.6;
    public static final double MAX_SCORE_SPEED = 0.3;
    private static final double paddingSize = 2.5;
    private static final double DEFAULT_TIME_STEP = 0.0001;
    private static final double STOPPING_THRESHOLD = 0.05;
    private final GolfCourse course;
    private final Expression expr;
    private double xinit, yinit;
    private double xMin, xMax, yMin, yMax;
    private double xTarget, yTarget;

    private final VectorDifferentiationFactory vectorDifferentiationFactory;
    private final ODESolver<StateVector4> solver;
    private final Differentiator differentiator;

    private final org.ken22.physics.differentiation.inplace.VectorDifferentiationFactory inPlaceVectorDifferentiationFactory;
    private final org.ken22.physics.odesolvers.inplace.ODESolver<StateVector4> inPlaceSolver;

    private ArrayList<StateVector4> trajectory = new ArrayList<>();
    private StateVector4 initialStateVector;
    private final double timeStep; // Default time step

    /// Constructors
    public PhysicsEngine(GolfCourse course) {
        this(course, new StateVector4(course.ballX(), course.ballY(), 0, 0));
    }

    public PhysicsEngine(GolfCourse course, StateVector4 initialStateVector) {
        this(course, initialStateVector, DEFAULT_TIME_STEP, new FivePointCenteredDifference(), new RK4());
    }

    public PhysicsEngine(GolfCourse course, StateVector4 initialStateVector, boolean completePhysics) {
        this(course, initialStateVector, DEFAULT_TIME_STEP, new FivePointCenteredDifference(), new RK4(), completePhysics);
    }

    public PhysicsEngine(GolfCourse course, StateVector4 initialStateVector, double timeStep) {
        this(course, initialStateVector, timeStep, new FivePointCenteredDifference(), new RK4());
    }

    public PhysicsEngine(GolfCourse course, StateVector4 initialStateVector, double timeStep,
                         Differentiator differentiator, ODESolver<StateVector4> solver) {
        this(course, initialStateVector, timeStep, differentiator, solver, false);
    }

    public PhysicsEngine (GolfCourse course, StateVector4 initialStateVector, double timeStep,
                          Differentiator differentiator, ODESolver<StateVector4> solver, boolean completePhysics) {
        if (timeStep > 0.033) {
            throw new IllegalArgumentException("Step size " + timeStep + " too large for 30FPS");
        }
        this.xinit = initialStateVector.x();
        this.yinit = initialStateVector.y();
        double vx = initialStateVector.vx();
        double vy = initialStateVector.vy();
        if (MathUtils.magnitude(vx, vy) > course.maximumSpeed) {
            throw new IllegalArgumentException("Initial vector speed too high! (max speed = "
                + course.maximumSpeed + ")");
        }
        this.course = course;
        this.xTarget = this.course.targetXcoord();
        this.yTarget = this.course.targetYcoord();
        this.initialStateVector = initialStateVector;
        this.timeStep = timeStep;
        this.differentiator = differentiator;
        this.solver = solver;
        this.expr = course.expression;
        this.vectorDifferentiationFactory = new VectorDifferentiationFactory(timeStep, expr, course, differentiator, completePhysics);
        trajectory.add(initialStateVector);
        this.inPlaceVectorDifferentiationFactory = new org.ken22.physics.differentiation.inplace.VectorDifferentiationFactory(timeStep, expr, course, differentiator, completePhysics);
        this.inPlaceSolver = new org.ken22.physics.odesolvers.inplace.RK4();

        this.xMin = xinit > course.targetXcoord() ? course.targetXcoord() - this.paddingSize : course.ballX() - this.paddingSize;
        this.xMax = xinit < course.targetXcoord() ? course.targetXcoord() +  this.paddingSize : course.ballX() + this.paddingSize;
        this.yMin = yinit > course.targetYcoord() ? course.targetYcoord() -  this.paddingSize : course.ballY() - this.paddingSize;
        this.yMax = yinit < course.targetYcoord() ? course.targetYcoord() +  this.paddingSize : course.ballY() + this.paddingSize;
    }

    /// Methods

    public StateVector4 getState() {
        return trajectory.getLast();
    }

    public void setState(StateVector4 stateVector) {
        if(stateVector == null) {
            throw new IllegalArgumentException("State vector cannot be null");
        }
        if(stateVector.vx() == 0.0 && stateVector.vy() == 0.0) {
            throw new IllegalArgumentException("State vector cannot have zero velocity");
        }

        if (MathUtils.magnitude(stateVector.vx(), stateVector.vy()) > 100) {
            throw new IllegalArgumentException("Initial vector speed too high! (max speed = "
                + 100 + ")"); //TODO use the max speed from settings
        }

        this.trajectory.clear();
        this.trajectory.add(stateVector);
    }

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
            trajectory.getLast().setX(trajectory.getLast().x() - timeStep * trajectory.getLast().vx());
            trajectory.getLast().setY(trajectory.getLast().y() - timeStep * trajectory.getLast().vy());
            //System.out.println("Out of bounds");
            return true;
        }

        double x = lastVector.x();
        double y = lastVector.y();
        double vx = lastVector.vx();
        double vy = lastVector.vy();

        // Evaluate the partial derivatives for x and y
        double dh_dx = MathUtils.xSlope(x, y, timeStep, expr, differentiator);
        double dh_dy = MathUtils.ySlope(x, y, timeStep, expr, differentiator);

        if (MathUtils.magnitude(vx, vy) < STOPPING_THRESHOLD) {
            if (MathUtils.magnitude(dh_dx, dh_dy) < STOPPING_THRESHOLD) {
                return true;
            } else if (course.staticFrictionGrass() > MathUtils.magnitude(dh_dx, dh_dy)) {
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

        return isAtRest() && MathUtils.magnitude(x - this.xTarget, y - this.yTarget) < targetRadius;
    }

    /**
     * Check if the ball has gone out of bounds.
     * @return TRUE if the ball is out of bounds, FALSE otherwise
     */
    public boolean outOfBounds() {
        boolean xout = trajectory.getLast().x() < xMin || trajectory.getLast().x() > xMax;
        boolean yout = trajectory.getLast().y() < yMin || trajectory.getLast().y() > yMax;
        return xout || yout;
    }

    private void treeCollision(StateVector4 state) {
        for(Tree t : course.getTrees()) {
            var distance = MathUtils.magnitude(state.x() - t.coordinates()[0], state.y() - t.coordinates()[1]);
            if (distance <= t.radius()) { //collision

                double[] unit_normal = new double[] {(state.x() - t.coordinates()[0]) / distance,
                    (state.y() - t.coordinates()[1]) / distance};
                double newvx = unit_normal[0]*BOUNCE_CONSTANT*MathUtils.magnitude(state.vx(), state.vy());
                double newvy = unit_normal[1]*BOUNCE_CONSTANT*MathUtils.magnitude(state.vx(), state.vy());
                state.setVx(newvx);
                state.setVy(newvy);
                System.out.println("Tree collision");
            }
        }
    }

    private void wallCollision(StateVector4 state) {
        for (Wall w : course.walls) {
            // Find the thickness offset
            double wid = w.thickness() / 2.0;

            // Calculate unit normal to the wall
            double[] unitNormal = MathUtils.unitNormal2D(w.startPoint()[0], w.startPoint()[1], w.endPoint()[0], w.endPoint()[1]);
            double xn = wid * unitNormal[0];
            double yn = wid * unitNormal[1];

            // Calculate actual corners of the wall using the normal
            double smx = w.startPoint()[0] - xn, smy = w.startPoint()[1] - yn;
            double spx = w.startPoint()[0] + xn, spy = w.startPoint()[1] + yn;
            double epx = w.endPoint()[0] + xn, epy = w.endPoint()[1] + yn;
            double emx = w.endPoint()[0] - xn, emy = w.endPoint()[1] - yn;

            // Calculate the bounding box for quick rejection
            double xMax = Math.max(smx, Math.max(spx, Math.max(epx, emx)));
            double xMin = Math.min(smx, Math.min(spx, Math.min(epx, emx)));
            double yMax = Math.max(smy, Math.max(spy, Math.max(epy, emy)));
            double yMin = Math.min(smy, Math.min(spy, Math.min(epy, emy)));

            // Quick rejection test
            if ( (state.x() <= xMax && state.x() >= xMin && state.y() <= yMax && state.y() >= yMin) &&
                MathUtils.pointInQuadrilateral(state.x(), state.y(), smx, smy, spx, spy, epx, epy, emx, emy)) {

                // Calculate mirror reflection of the ball from the wall
                double[] temp = MathUtils.reflectedVector2D(xn, yn, state.vx(), state.vy());
                double v = MathUtils.magnitude(state.vx(), state.vy());
                double[] reflected_velocities = MathUtils.multiply(temp, 0.8 * v);
                state.setVx(reflected_velocities[0]);
                state.setVy(reflected_velocities[1]);
            }
        }
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
        if (MathUtils.magnitude(vx, vy) < STOPPING_THRESHOLD) {
            differentiation = vectorDifferentiationFactory.lowSpeedVectorDifferentiation4();
        } else {
            differentiation = vectorDifferentiationFactory.normalSpeedVectorDifferentiation4();
        }

        StateVector4 newVector = solver.nextStep(timeStep, lastVector, differentiation);

        treeCollision(newVector);
        wallCollision(newVector);

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
//        StateVector4 currentState =
//            new StateVector4(trajectory.getLast().x(), trajectory.getLast().y(), trajectory.getLast().vx(), trajectory.getLast().vy());
//
//        while (!isAtRest()) {
//            InPlaceVectorDifferentiation4 inPlaceDifferentiation;
//            // Decide which equations to use for updating the acceleration
//            if (MathUtils.magnitude(currentState.vx(), currentState.vy()) < STOPPING_THRESHOLD) {
//                inPlaceDifferentiation = inPlaceVectorDifferentiationFactory.lowSpeedVectorDifferentiation4();
//            } else {
//                inPlaceDifferentiation = inPlaceVectorDifferentiationFactory.normalSpeedVectorDifferentiation4();
//            }
//
//            inPlaceSolver.nextStep(timeStep, currentState, inPlaceDifferentiation);
//
//            treeCollision(currentState);
//            wallCollision(currentState);
//        }
//
//        return currentState;
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
        private static final int FRAME_RATE = 30;
        private static final double SPEED = 0.7;
        private final int kPerFrame = (int) ((SPEED / FRAME_RATE) / timeStep);
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

        public StateVector4 last() {
            return trajectory.get(trajectory.size() - 1);
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

    public void writeToCSV(String path, String name) {
        File directory = new File(path);

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directory created: " + "core/src/test/resources/ai/hillclimber/handpicked/jsons");
            } else {
                throw new RuntimeException("Failed to create directory: " + "core/src/test/resources/ai/hillclimber/handpicked/jsons");
            }
        }

        // Check if the directory exists, if not, create it
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directory created: " + "core/src/test/resources/ai/hillclimber/handpicked/solutions");
            } else {
                throw new RuntimeException("Failed to create directory: " + "core/src/test/resources/ai/hillclimber/handpicked/solutions");
            }
        }

        File csv = new File(path+File.separator+name+".csv");
        try {
            FileWriter writer = new FileWriter(csv);
            writer.write("x,y,vx,vy\n");
            for(StateVector4 state : trajectory) {
                writer.write(state.x() + "," + state.y() + "," + state.vx() + "," + state.vy() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public double getyTarget() {
        return yTarget;
    }

    public double getxTarget() {
        return xTarget;
    }
}
