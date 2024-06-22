package org.ken22.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.ken22.ai.hillclimbing.HillClimber;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.engine.PhysicsEngine;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.utils.MathUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class HillClimberTest {

    private double tolerance = 0.02;

    @DisplayName("Setup testing for handpicked tests")
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

//                    double xMin = x > xt ? xt - paddingSize : x - paddingSize;
//                    double xMax = x < xt ? xt +  paddingSize : x + paddingSize;
//                    double yMin = y > yt ? yt -  paddingSize : y - paddingSize;
//                    double yMax = y < yt ? yt +  paddingSize : y + paddingSize;

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

    @Test
    @DisplayName("Flat plane with good initial guess velocity")
    void flatPlaneGoodInitGuess() {
        double vx =
        testManualSpeedCourse("0.5", 4.491572403911195, 1.6239689874674115);
    }

    @Test
    @DisplayName("Flat plane")
    void flatPlaneEuclidian2D() {
        Random gen = new Random();
        GolfCourse course = randomCourse("Flat plane", MathUtils.courses[3], gen);

//                    double xMin = x > xt ? xt - paddingSize : x - paddingSize;
//                    double xMax = x < xt ? xt +  paddingSize : x + paddingSize;
//                    double yMin = y > yt ? yt -  paddingSize : y - paddingSize;
//                    double yMax = y < yt ? yt +  paddingSize : y + paddingSize;

        StateVector4 initialState = generateInitialVector(course.ballX(), course.ballY(), gen);
        System.out.println("Terrain equation: z=" + course.courseProfile());
        System.out.println("Starting vector: " + initialState);

        HillClimber climber = new HillClimber(course, initialState, Heuristic.EUCLIDIAN2D);
        System.out.println("Heuristic: " + Heuristic.EUCLIDIAN2D);
        StateVector4 solution = climber.search();
        System.out.println("Solution: " + solution);
        PhysicsEngine engine = new PhysicsEngine(course, solution);
        engine.solve();
        engine.writeToCSV("core/src/test/resources/ai/hillclimber/statistical/solutions", "Flat Plane");
        double[] result = checkFinalState(engine.getTrajectory().getLast(), course);  // {distance, finalSpeed}
        assertAll(
            () -> assertEquals(result[0], MathUtils.targetRadius, this.tolerance),
            () -> assertTrue(result[1] < PhysicsEngine.MAX_SCORE_SPEED)
        );
    }

    @Test
    @DisplayName("Statistical tests")
    void allStatisticalTests() {
        Random gen = new Random();
        ObjectMapper objectMapper = new ObjectMapper();

        File statisticalSolutions = new File("src/test/resources/ai/hillclimber/statistical/solutions");
        if (!statisticalSolutions.isDirectory()) {
            throw new RuntimeException("Path is not a directory: " + "core/src/test/resources/ai/hillclimber/statistical/solutions");
        }

        File statisticalJSONs = new File("src/test/resources/ai/hillclimber/statistical/jsons");
        if (!statisticalJSONs.isDirectory()) {
            throw new RuntimeException("Path is not a directory: " + "core/src/test/resources/ai/hillclimber/statistical/jsons");
        }

        File[] files = statisticalJSONs.listFiles();
        int name = 0;
        ArrayList<Executable> tests = new ArrayList<>();
        for (File file : files) {
            try {
                GolfCourse course = objectMapper.readValue(file, GolfCourse.class);
                double x = gen.nextDouble()*20-10;
                double y = gen.nextDouble()*20-10;
                double vx = gen.nextDouble()*10-5;
                double vymax = Math.sqrt(25-vx*vx);
                double vy = gen.nextDouble()*2*vymax-vymax-0.0001;
                StateVector4 initialState = generateInitialVector(x, y, gen);
                System.out.println("Starting vector: " + initialState);
                HillClimber climber = new HillClimber(course, initialState, Heuristic.EUCLIDIAN2D);
                System.out.println("Searching for solution " + name);
                System.out.println("Terrain: " + course.courseProfile());
                StateVector4 solution = climber.search();
                System.out.println("Solution: " + solution);
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
                name++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        assertAll(tests);
    }

    /**
     * Shoot in specified direction
     * @param terrain
     * @param vx
     * @param vy
     */
    public void testManualSpeedCourse(String terrain, double vx, double vy) {
        Random gen = new Random();
        GolfCourse course = randomCourse(terrain, terrain, gen);
        double x = course.ballX();
        double y = course.ballY();
        StateVector4 initialState = new StateVector4(x, y, vx, vy);
        System.out.println("Starting vector: " + initialState);
        HillClimber climber = new HillClimber(course, initialState, Heuristic.EUCLIDIAN2D);
        System.out.println("Terrain equation: z=" + course.courseProfile());
        System.out.println("Heuristic: " + Heuristic.EUCLIDIAN2D);
        StateVector4 solution = climber.search();
        System.out.println("Solution: " + solution);
        PhysicsEngine engine = new PhysicsEngine(course, solution, true);
        engine.solve();
        engine.writeToCSV("core/src/test/resources/ai/hillclimber/statistical/solutions", "Flat Plane");
        double[] result = checkFinalState(engine.getTrajectory().getLast(), course);  // {distance, finalSpeed}
        assertAll(
            () -> assertEquals(MathUtils.targetRadius, result[0], this.tolerance),
            () -> assertTrue(result[1] < PhysicsEngine.MAX_SCORE_SPEED)
        );
    }

    public void testManualSpeedCourseTarget(String terrain, double vx, double vy, double xt, double yt) {
        Random gen = new Random();
        GolfCourse course = randomCourse(terrain, terrain, gen);
        double x = course.ballX();
        double y = course.ballY();
        StateVector4 initialState = new StateVector4(x, y, vx, vy);
        System.out.println("Starting vector: " + initialState);
        HillClimber climber = new HillClimber(course, initialState, Heuristic.EUCLIDIAN2D);
        System.out.println("Terrain equation: z=" + course.courseProfile());
        System.out.println("Heuristic: " + Heuristic.EUCLIDIAN2D);
        StateVector4 solution = climber.search();
        System.out.println("Solution: " + solution);
        PhysicsEngine engine = new PhysicsEngine(course, solution, true);
        engine.solve();
        engine.writeToCSV("core/src/test/resources/ai/hillclimber/statistical/solutions", "Flat Plane");
        double[] result = checkFinalState(engine.getTrajectory().getLast(), course);  // {distance, finalSpeed}
        assertAll(
            () -> assertEquals(MathUtils.targetRadius, result[0], this.tolerance),
            () -> assertTrue(result[1] < PhysicsEngine.MAX_SCORE_SPEED)
        );
    }

    public void testManualSpeedCourseTargetPos(String terrain, double vx, double vy, double xt, double yt, double x, double y) {

    }

    public void testManualCourse(String terrain) {
        Random gen = new Random();
        GolfCourse course = randomCourse(terrain, terrain, gen);

        StateVector4 initialState = generateInitialVector(course.ballX(), course.ballY(), gen);
        System.out.println("Terrain equation: z=" + course.courseProfile());
        System.out.println("Starting vector: " + initialState);

        HillClimber climber = new HillClimber(course, initialState, Heuristic.EUCLIDIAN2D);
        System.out.println("Heuristic: " + Heuristic.EUCLIDIAN2D);
        StateVector4 solution = climber.search();
        System.out.println("Solution: " + solution);
        PhysicsEngine engine = new PhysicsEngine(course, solution);
        engine.solve();
        engine.writeToCSV("core/src/test/resources/ai/hillclimber/statistical/solutions", "Flat Plane");
        double[] result = checkFinalState(engine.getTrajectory().getLast(), course);  // {distance, finalSpeed}
        assertAll(
            () -> assertEquals(result[0], MathUtils.targetRadius, this.tolerance),
            () -> assertTrue(result[1] < PhysicsEngine.MAX_SCORE_SPEED)
        );
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
        return new GolfCourse(name, terrain, MathUtils.range, 1, MathUtils.g, kf, sf, kf_s,
            sf_s, 5.0, 0.1, xt, yt,x,y);
    }

    public StateVector4 generateInitialVector(double x, double y, Random gen) {

        double vx = gen.nextDouble()*10-5;
        double vymax = Math.sqrt(25-vx*vx);
        double vy = gen.nextDouble()*2*vymax-vymax-0.0001;
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

    public GolfCourse randomCourse(String name) {
        Random gen = new Random();
        double kf = gen.nextDouble()*0.2;
        double sf = gen.nextDouble()*0.1;
        double kf_s = gen.nextDouble()*0.7;
        double sf_s = gen.nextDouble()*0.6;
        double x = gen.nextDouble()*20-10;
        double y = gen.nextDouble()*20-10;
        double xt = gen.nextDouble()*20-10;
        double yt = gen.nextDouble()*20-10;;
        int courseidx = gen.nextInt(MathUtils.courses.length);
        return new GolfCourse(name, MathUtils.courses[courseidx], MathUtils.range, 1, MathUtils.g, kf, sf, kf_s,
            sf_s, 5.0, 0.1, xt, yt,x,y);
    }
}
