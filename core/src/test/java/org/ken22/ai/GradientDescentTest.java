package org.ken22.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.ken22.input.settings.DifferentiatorType;
import org.ken22.input.settings.GeneralSettings;
import org.ken22.input.settings.ODESolverType;
import org.ken22.obstacles.SandPit;
import org.ken22.obstacles.Tree;
import org.ken22.physics.PhysicsFactory;
import org.ken22.players.bots.simulatedannealing.GradientDescent;
import org.ken22.input.courseinput.CourseParser;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.engine.PhysicsEngine;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.error.ErrorFunction;
import org.ken22.players.error.GradientDescent2;
import org.ken22.utils.MathUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p> Every test has a tag that specifies each of the following parameters, in this exact order:</p>
 * <ul>
 *     <li>Physics equations:
 *         {@link org.ken22.physics.differentiation.InstantaneousVectorDifferentiationFactory} (simple),
 *         {@link org.ken22.physics.differentiation.InstVecDiffFactoryComplete} (complete)
 *     </li>
 *     <li>Solver: RK4 {@link org.ken22.physics.odesolvers.RK4}</li>
 *     <li>Terrain:
 *         Flat, incline, exponential (single hill/trough), sinusoid (many hills/troughs)
 *         {@link org.ken22.utils.MathUtils}
 *     </li>
 *     <li>Heuristic:
 *         Euclidian2D {@link org.ken22.players.error.EuclideanError} (x and y distance),
 *         EuclAndVelError {@link org.ken22.players.error.EuclAndVelError} (x and y distance and velocity error),
 *         Heuristic2 {@link GradientDescent2} (custom heuristic)
 *     </li>
 *     <li>Initial guess: good guess, random guess</li>
 * </ul>
 * <p>Other controlled parameters (that were not tested for) include:</p>
 * <ul>
 *     <li>Differentiation: default 5 point centred difference {@link org.ken22.physics.differentiators.FivePointCenteredDifference}</li>
 *     <li>Friction coefficients: default 0.1 kinetic, 0.05 static</li>
 * </ul>
 */
@Tags ({
    @Tag("GradientDescent")
    })
public class GradientDescentTest {

    /******************************************************************************************************************

     Fields

     *****************************************************************************************************************/

    private double tolerance = 0.02;

    /******************************************************************************************************************

     Setup

     *****************************************************************************************************************/

    @DisplayName("Setup testing for statistical tests")
    @BeforeAll
    public static void setupStatisticalTesting() {
        File statisticalJSONs = new File("src/test/resources/ai/hillclimber/statistical/jsons");
        File statisticalSolutions = new File("src/test/resources/ai/hillclimber/statistical/solutions");
        Random gen = new Random();
        ObjectMapper objectMapper = new ObjectMapper();

        // Check if the directory exists, if not, create it
        if (!statisticalJSONs.exists()) {
            if (statisticalJSONs.mkdirs()) {
                System.out.println("Directory created: " + "core/src/test/resources/ai/hillclimber/statistical/jsons");
            } else {
                throw new RuntimeException("Failed to create directory: " + "core/src/test/resources/ai/hillclimber/statistical/jsons");
            }
        }

        // Check if the directory exists, if not, create it
        if (!statisticalSolutions.exists()) {
            if (statisticalSolutions.mkdirs()) {
                System.out.println("Directory created: " + "core/src/test/resources/ai/hillclimber/statistical/solutions");
            } else {
                throw new RuntimeException("Failed to create directory: " + "core/src/test/resources/ai/hillclimber/statistical/solutions");
            }
        }

        String[] contents = statisticalJSONs.list();
        try {
            if (contents.length == 0) {
                int sum = 0;
                for(int i = 0; i < 4; i++) {
                    double kf = gen.nextDouble()*0.2;
                    double sf = gen.nextDouble()*0.1;
                    double kf_s = gen.nextDouble()*0.7;
                    double sf_s = gen.nextDouble()*0.6;
                    double x = gen.nextDouble()*20-10;
                    double y = gen.nextDouble()*20-10;
                    double xt = gen.nextDouble()*20-10;
                    double yt = gen.nextDouble()*20-10;;
                    String name = "test" + sum;
                    int courseidx = gen.nextInt(MathUtils.courses.length);

                    GolfCourse course = new GolfCourse(name, MathUtils.courses[courseidx], MathUtils.range, 1, MathUtils.g, kf, sf, kf_s,
                        sf_s, 5.0, 0.1, xt, yt,x,y);
                    objectMapper.writeValue(new File(statisticalJSONs, name + ".json"), course);
                    sum++;
                }
            } else {
                System.out.println("Directory is not empty. Skipping creation of JSON files.");
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeAll
    @DisplayName("Setup testing for handpicked tests")
    static void setupHandiPickedTests() {

        File statisticalJSONs = new File("src/test/resources/ai/hillclimber/handpicked/jsons");
        File statisticalSolutions = new File("src/test/resources/ai/hillclimber/handpicked/solutions");
        // Check if the directory exists, if not, create it

        if (!statisticalJSONs.exists()) {
            if (statisticalJSONs.mkdirs()) {
                System.out.println("Directory created: " + "core/src/test/resources/ai/hillclimber/handpicked/jsons");
            } else {
                throw new RuntimeException("Failed to create directory: " + "core/src/test/resources/ai/hillclimber/handpicked/jsons");
            }
        }

        // Check if the directory exists, if not, create it
        if (!statisticalSolutions.exists()) {
            if (statisticalSolutions.mkdirs()) {
                System.out.println("Directory created: " + "core/src/test/resources/ai/hillclimber/handpicked/solutions");
            } else {
                throw new RuntimeException("Failed to create directory: " + "core/src/test/resources/ai/hillclimber/handpicked/solutions");
            }
        }
    }

    /******************************************************************************************************************

     Simple physics

     *****************************************************************************************************************/

    @Test
    @Tag("SimplePhys")
    @Tag("RK4")
    @Tag("FlatPlane")
    @Tag("Euclidian2D")
    @Tag("GoodInitialGuess")
    @Tag("SimplePhysics")
    @DisplayName("Flat plane, good initial velocity, Euclidian 2")
    void flatPlaneGoodInitGuess() {
        Random gen  = new Random();
        double x = gen.nextDouble()*20-10;
        double y = gen.nextDouble()*20-10;
        double xt = gen.nextDouble()*20-10;
        double yt = gen.nextDouble()*20-10;
        double vx = (xt - x)/4;
        double vy = (yt - y)/4;
        testManual(MathUtils.courses[3], vx, vy, x, y);
    }

    @Test
    @Tag("SimplePhys")
    @Tag("RK4")
    @Tag("FlatPlane")
    @Tag("Euclidian2D")
    @Tag("RandomInitialGuess")
    @Tag("SimplePhysics")
    @DisplayName("Flat plane, random initial guess, Euclidian 2")
    void flatPlaneEuclidian2D() {
        Random gen = new Random();
        testManual(MathUtils.courses[3]);
    }

    @Test
    @Tag("SimplePhys")
    @Tag("RK4")
    @Tag("FlatPlane")
    @Tag("Heuristic1")
    @Tag("RandomInitialGuess")
    @Tag("SimplePhysics")
    @DisplayName("Flat plane, good initial velocity, Heuristic1")
    void flatPlaneGoodInitGuessHeuristic1() {
        Random gen  = new Random();
        double x = gen.nextDouble()*20-10;
        double y = gen.nextDouble()*20-10;
        double xt = gen.nextDouble()*20-10;
        double yt = gen.nextDouble()*20-10;
        double vx = (xt - x)/4;
        double vy = (yt - y)/4;
        testManual(MathUtils.courses[3], vx, vy, x, y, new GradientDescent2());
    }

    @Test
    @Tag("SimplePhys")
    @Tag("RK4")
    @Tag("FlatPlane")
    @Tag("Heuristic2")
    @Tag("RandomInitialGuess")
    @Tag("SimplePhysics")
    @DisplayName("Flat plane, random initial guess, Heuristic2")
    void flatPlaneRandomInitGuessHeuristic1() {
        testManual(MathUtils.courses[3], new GradientDescent2());
    }

    @Test
    @Tag("SimplePhys")
    @Tag("RK4")
    @Tag("Statistical")
    @Tag("Heuristic2")
    @Tag("RandomInitialGuess")
    @Tag("SimplePhysics")
    @DisplayName("Statistical tests")
    void allStatisticalTests() {

        File statisticalSolutions = new File("src/test/resources/ai/hillclimber/statistical/solutions");
        if (!statisticalSolutions.isDirectory()) {
            throw new RuntimeException("Path is not a directory: " + "core/src/test/resources/ai/hillclimber/statistical/solutions");
        }

        File statisticalJSONs = new File("src/test/resources/ai/hillclimber/statistical/jsons");
        if (!statisticalJSONs.isDirectory()) {
            throw new RuntimeException("Path is not a directory: " + "core/src/test/resources/ai/hillclimber/statistical/jsons");
        }
        else if (statisticalJSONs.listFiles().length == 0) {
            throw new RuntimeException("No JSON files found in: " + "core/src/test/resources/ai/hillclimber/statistical/jsons");
        }

        File[] files = statisticalJSONs.listFiles();
        int name = 0;
        ArrayList<Executable> tests = new ArrayList<>();
        for (File file : files) {
            try {
                testFromJSON(file, "test" + name, tests);
                name++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        assertAll(tests);
    }

    /******************************************************************************************************************

     Complete Physics

     *****************************************************************************************************************/





    /******************************************************************************************************************

     Helper methods

     *****************************************************************************************************************/

    /**
    * Shoot in specified direction
    * @param terrain
    * @param vx
    * @param vy
    */
    public void testManual(String terrain, double vx, double vy) {
        Random gen = new Random();
        GolfCourse course = randomCourse(terrain, terrain, gen);
        double x = course.ballX();
        double y = course.ballY();
        StateVector4 initialState = new StateVector4(x, y, vx, vy);
        GradientDescent climber = new GradientDescent(course, createPhysicsFactory());
        StateVector4 solution = climber.play(initialState);
        PhysicsEngine engine = new PhysicsEngine(course, solution, true);
        engine.solve();
        engine.writeToCSV("core/src/test/resources/ai/hillclimber/statistical/solutions", "Flat Plane");
        double[] result = checkFinalState(engine.getTrajectory().getLast(), course);  // {distance, finalSpeed}
        assertAll(
            () -> assertEquals(MathUtils.targetRadius, result[0], this.tolerance),
            () -> assertTrue(result[1] < PhysicsEngine.MAX_SCORE_SPEED)
        );
    }

    public void testManual(String terrain, double vx, double vy, double x, double y) {
        Random gen = new Random();
        GolfCourse course = randomCourse(terrain, terrain, gen);
        StateVector4 initialState = new StateVector4(x, y, vx, vy);
        GradientDescent climber = new GradientDescent(course, createPhysicsFactory());
        StateVector4 solution = climber.play(initialState);
        PhysicsEngine engine = new PhysicsEngine(course, solution, true);
        engine.solve();
        engine.writeToCSV("core/src/test/resources/ai/hillclimber/statistical/solutions", "Flat Plane");
        double[] result = checkFinalState(engine.getTrajectory().getLast(), course);  // {distance, finalSpeed}
        assertAll(
            () -> assertEquals(MathUtils.targetRadius, result[0], this.tolerance),
            () -> assertTrue(result[1] < PhysicsEngine.MAX_SCORE_SPEED)
        );
    }

    public void testManual(String terrain, double vx, double vy, double x, double y, ErrorFunction heur) {
        Random gen = new Random();
        GolfCourse course = randomCourse(terrain, terrain, gen);
        StateVector4 initialState = new StateVector4(x, y, vx, vy);
        GradientDescent climber = new GradientDescent(course, createPhysicsFactory(), heur);
        StateVector4 solution = climber.play(initialState);
        PhysicsEngine engine = new PhysicsEngine(course, solution, true);
        engine.solve();
        engine.writeToCSV("core/src/test/resources/ai/hillclimber/statistical/solutions", "Flat Plane");
        double[] result = checkFinalState(engine.getTrajectory().getLast(), course);  // {distance, finalSpeed}
        assertAll(
            () -> assertEquals(MathUtils.targetRadius, result[0], this.tolerance),
            () -> assertTrue(result[1] < PhysicsEngine.MAX_SCORE_SPEED)
        );
    }

    public PhysicsFactory createPhysicsFactory() {
        return new PhysicsFactory(new GeneralSettings(ODESolverType.RK4, 0.0001,
            DifferentiatorType.FIVE_POINT_CENTERED, true, true));
    }

    public PhysicsFactory createPhysicsFactory(ODESolverType solverType, double stepSize,
                                               DifferentiatorType differ, boolean simplePhysics, boolean allowPlaying) {
        return new PhysicsFactory(new GeneralSettings(solverType, stepSize, differ, simplePhysics, allowPlaying));
    }

    public PhysicsFactory createPhysicsFactory(boolean simplePhysics) {
        return new PhysicsFactory(new GeneralSettings(ODESolverType.RK4, 0.0001,
            DifferentiatorType.FIVE_POINT_CENTERED, simplePhysics, true));
    }

    public void testManual(String terrain) {
        Random gen = new Random();
        GolfCourse course = randomCourse(terrain, terrain, gen);
        StateVector4 initialState = generateInitialVector(course.ballX(), course.ballY(), gen);
        GradientDescent climber = new GradientDescent(course, createPhysicsFactory());
        StateVector4 solution = climber.play(initialState);
        PhysicsEngine engine = new PhysicsEngine(course, solution);
        engine.solve();
        engine.writeToCSV("core/src/test/resources/ai/hillclimber/statistical/solutions", "Flat Plane");
        double[] result = checkFinalState(engine.getTrajectory().getLast(), course);  // {distance, finalSpeed}
        assertAll(
            () -> assertEquals(result[0], MathUtils.targetRadius, this.tolerance),
            () -> assertTrue(result[1] < PhysicsEngine.MAX_SCORE_SPEED)
        );
    }

    public void testManual(String terrain, ErrorFunction heur) {
        Random gen = new Random();
        GolfCourse course = randomCourse(terrain, terrain, gen);
        StateVector4 initialState = generateInitialVector(course.ballX(), course.ballY(), gen);
        GradientDescent climber = new GradientDescent(course, createPhysicsFactory(), heur);
        StateVector4 solution = climber.play(initialState);
        PhysicsEngine engine = new PhysicsEngine(course, solution);
        engine.solve();
        engine.writeToCSV("core/src/test/resources/ai/hillclimber/statistical/solutions", "Flat Plane");
        double[] result = checkFinalState(engine.getTrajectory().getLast(), course);  // {distance, finalSpeed}
        assertAll(
            () -> assertEquals(result[0], MathUtils.targetRadius, this.tolerance),
            () -> assertTrue(result[1] < PhysicsEngine.MAX_SCORE_SPEED)
        );
    }

    public void testFromJSON(File file, String name, ArrayList<Executable> tests) throws IOException {
        Random gen = new Random();
        GolfCourse course = (new CourseParser(file)).getCourse();
        double x = gen.nextDouble()*20-10;
        double y = gen.nextDouble()*20-10;
        StateVector4 initialState = generateInitialVector(x, y, gen);
        GradientDescent climber = new GradientDescent(course, createPhysicsFactory());
        StateVector4 solution = climber.play(initialState);
        PhysicsEngine engine = new PhysicsEngine(course, solution);
        engine.solve();
        engine.writeToCSV("core/src/test/resources/ai/hillclimber/statistical/solutions", ""+name);
        double[] results = checkFinalState(engine.getTrajectory().getLast(), course);  // {distance, finalSpeed}
        tests.add(() -> {
            assertAll(
                () -> assertEquals(results[0], MathUtils.targetRadius, this.tolerance),
                () -> assertTrue(results[1] < PhysicsEngine.MAX_SCORE_SPEED)
            );
        });
    }

    public GolfCourse randomCourse(String name, String terrain, Random gen) {
        double kf = gen.nextDouble()*0.2;
        double sf = gen.nextDouble()*0.1;
        double kf_s = gen.nextDouble()*0.7;
        double sf_s = gen.nextDouble()*0.6;
        double x = gen.nextDouble()*20-10;
        double y = gen.nextDouble()*20-10;
        double xt = gen.nextDouble()*20-10;
        double yt = gen.nextDouble()*20-10;;
        List<Tree> trees = null;
        List<SandPit> sandpits = null;
        return (new GolfCourse(name, terrain, MathUtils.range, 1, MathUtils.g, kf, sf, kf_s,
            sf_s, 5.0, 0.1, xt, yt,x,y));
    }

    public StateVector4 generateInitialVector(double x, double y, Random gen) {
        double vx = gen.nextDouble()*10-5;
        double vymax = Math.sqrt(25-vx*vx);
        double vy = gen.nextDouble()*2*vymax-vymax-0.05; //GradientDescent delta, to prevent the delta from overshooting
        return new StateVector4(x,y,vx,vy);
    }

    public double[] checkFinalState(StateVector4 finalsv, GolfCourse course) {
        double finalX = finalsv.x();
        double finalY = finalsv.y();
        double finalvx = finalsv.vx();
        double finalvy = finalsv.vy();
        double xt = course.targetXcoord();
        double yt = course.targetYcoord();
        double distance = MathUtils.magnitude(finalX-xt, finalY-yt);
        double finalSpeed = MathUtils.magnitude(finalvx, finalvy);
        System.out.println("Final distance: " + distance + ", target reached: " + (distance < MathUtils.targetRadius));
        System.out.println("Final speed: " + finalSpeed + ", slow enough: " + (finalSpeed < PhysicsEngine.MAX_SCORE_SPEED));
        return new double[]{distance, finalSpeed};
    }
}
