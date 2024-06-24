package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.objecthunter.exp4j.Expression;
import org.ken22.game.GameLoop;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.models.*;
import org.ken22.obstacles.Tree;
import org.ken22.physics.engine.PhysicsEngine;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.bots.BotFactory;
import org.ken22.players.HumanPlayer;
import org.ken22.players.bots.*;
import org.ken22.players.bots.hillclimbing.GradientDescent;
import org.ken22.players.bots.hillclimbing.SimulatedAnnealing;
import org.ken22.players.bots.otherhillclimbing.HillClimbingBot;
import org.ken22.players.bots.otherhillclimbing.LineHillClimbingBot;
import org.ken22.players.bots.otherhillclimbing.RandomRestartHillClimbingBot;
import org.ken22.players.bots.simplebots.InitialGuessBot;
import org.ken22.players.bots.simplebots.SimplePlanarApproximationBot;
import org.ken22.utils.GolfExpression;

import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GolfScreen extends ScreenAdapter {

    private static final Logger LOGGER = Logger.getLogger(GolfScreen.class.getName());
    static {

        // the default level is INFO
        // if you want to change logging, just change the enum type at (1) and (2)
        // https://docs.oracle.com/javase/8/docs/api/java/util/logging/Level.html
        LOGGER.setLevel(Level.FINER); // (1)

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE); // (2)
        LOGGER.addHandler(consoleHandler);
    }
    public static final float PADDING_SIZE = 2.5f;
    private float xMin, xMax, yMin, yMax;

    private ScreenManager manager;

    private Viewport viewport;
    private PerspectiveCamera camera;
    private FirstPersonCameraController controller;

    private ModelBatch[][] modelBatches;
    private ModelBatch[][] shadowBatches;
    private ModelInstance[][] terrainInstances;

    private ModelBuilder waterBuilder;
    private MeshPartBuilder waterMeshPartBuilder;
    private ModelInstance waterInstance;
    private ModelBatch waterBatch;

    private ArrayList<ModelInstance> treeInstances;
    private ModelBatch treeBatch;
    private ModelBatch treeShadowBatch;
    private ArrayList<ModelInstance> treeCrownInstances;
    private ModelBatch treeCrownBatch;
    private ModelBatch treeCrownShadowBatch;

    private ModelInstance golfBallInstance;
    private ModelBatch golfBallBatch;
    private ModelBatch golfBallShadowBatch;

    private ModelInstance cylinderInstance;
    private ModelInstance poleInstance;
    private ModelBatch targetBatch;

    private Environment environment;

    private BotFactory botFactory;
    private InitialGuessBot initialGuessBot;
    private SimplePlanarApproximationBot simpleBot;
    private HillClimbingBot hillClimbingBot;
    private LineHillClimbingBot lineHillClimbingBot;
    private RandomRestartHillClimbingBot randomRestartHillClimbingBot;
    private NewtonRaphsonBot newtonRaphsonBot;
    private GradientDescent gradientDescent;
    private HumanPlayer humanPlayer;
    private SimulatedAnnealing simulatedAnnealing;

    private GameLoop gameLoop;

    private PhysicsEngine engine;
    private PhysicsEngine.FrameRateIterator iterator;
    private StateVector4 currentState;

    private GolfCourse course;
    private Expression expr;



    /**
     * Everything in GolfScreen is initialized here, rather than in the show() method.
     * This is because the show() method is only called when the screen is set as the current screen
     * in the Game class, which is not the case here.
     */
    public GolfScreen(ScreenManager manager, GolfCourse course, BotFactory botFactory, GameLoop gameLoop) {
        this.manager = manager;
        this.course = course;
        this.expr = GolfExpression.expr(course);
        this.currentState = new StateVector4(course.ballX(), course.ballY(), 0, 0);
        this.botFactory = botFactory;
        this.gameLoop = gameLoop;
        gameLoop.setGolfScreen(this);

        initialGuessBot = botFactory.initialGuessBot(course);
        simpleBot = botFactory.planarApproximationBot(course);
        hillClimbingBot = botFactory.hillClimbingBot(course, initialGuessBot);
        lineHillClimbingBot = botFactory.lineHillClimbingBot(course, initialGuessBot);
        randomRestartHillClimbingBot = botFactory.randomRestartHillClimbingBot(course, initialGuessBot);
        gradientDescent = botFactory.gradientDescent(course);
        newtonRaphsonBot = botFactory.newtonRaphsonBot(course, initialGuessBot);
        simulatedAnnealing = botFactory.simulatedAnnealing(course);
        //humanPlayer = new HumanPlayer();

        // Set map limits
        xMin = (float) (course.ballX() > course.targetXcoord() ? course.targetXcoord() -  PADDING_SIZE : course.ballX() - PADDING_SIZE);
        xMax = (float) (course.ballX() < course.targetXcoord() ? course.targetXcoord() +  PADDING_SIZE : course.ballX() + PADDING_SIZE);
        yMin = (float) (course.ballY() > course.targetYcoord() ? course.targetYcoord() -  PADDING_SIZE : course.ballY() - PADDING_SIZE);
        yMax = (float) (course.ballY() < course.targetYcoord() ? course.targetYcoord() +  PADDING_SIZE : course.ballY() + PADDING_SIZE);

        // Initialize the environment
        environment = new Environment();
        environment.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, -1f, -1f, -1f));
        environment.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, -1f, -1f, 1f));
        environment.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, 1f, -1f, -1f));
        environment.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, 1f, -1f, 1f));

        // create terrain model
        TerrainModel terrainModel= new TerrainModel(expr, xMin, xMax, yMin, yMax);
        modelBatches = terrainModel.getModelBatches();
        shadowBatches = terrainModel.getShadowBatches();
        terrainInstances = terrainModel.getTerrainInstances();

        // Create water model
        WaterModel waterModel = new WaterModel(-0.005f, xMin, xMax, yMin, yMax);
        waterInstance = waterModel.getWaterInstance();
        waterBatch = waterModel.getWaterBatch();

        // Create tree models
        treeInstances = new ArrayList<>();
        treeBatch = new ModelBatch();
        treeShadowBatch = new ModelBatch(new DepthShaderProvider());
        treeCrownInstances = new ArrayList<>();
        treeCrownBatch = new ModelBatch();
        treeCrownShadowBatch = new ModelBatch(new DepthShaderProvider());
        for (Tree t : course.trees) {
            TreeModel treeModel = new TreeModel((float) t.radius());
            var treeInstance = treeModel.getTreeInstance();
            var treeBaseHeight = (float) expr
                .setVariable("x", t.coordinates()[0])
                .setVariable("y", t.coordinates()[1])
                .evaluate();
            treeInstance.transform.setTranslation((float) t.coordinates()[0], treeBaseHeight, (float) t.coordinates()[1]);
            treeInstances.add(treeInstance);
            var treeCrownInstance = treeModel.getCrownInstance();
            treeCrownInstance.transform.setTranslation((float) t.coordinates()[0], treeBaseHeight + treeModel.getHeight(), (float) t.coordinates()[1]);
            treeCrownInstances.add(treeCrownInstance);
        }

        // Create golf ball model
        GolfBallModel golfBallModel = new GolfBallModel();
        golfBallInstance = golfBallModel.getModelInstance();
        golfBallBatch = new ModelBatch();
        golfBallInstance.transform.setTranslation((float) course.ballX(), 0f, (float) course.ballY());
        golfBallShadowBatch = new ModelBatch(new DepthShaderProvider());

        // Create target model
        TargetModel targetModel = new TargetModel((float) course.targetRadius()+0.15f); // magic number to render properly
        cylinderInstance = targetModel.getCylinderInstance();
        poleInstance = targetModel.getPoleInstance();
        targetBatch = new ModelBatch();

        var targetHeight = expr.setVariable("x", course.targetXcoord()).setVariable("y", course.targetYcoord()).evaluate();
        cylinderInstance.transform.setTranslation((float) course.targetXcoord(), (float) targetHeight, (float) course.targetYcoord());
        poleInstance.transform.setTranslation((float) course.targetXcoord(), (float) targetHeight,  (float) course.targetYcoord());

        // Initialize the camera
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set((float)course.ballX(), 0f, (float)course.ballY());
        camera.lookAt(1f, 0f, 1f);
        camera.near = 0.1f;
        camera.far = 300f;
        camera.update();
        // Create a viewport
        viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        // Initialize the FirstPersonCameraController
        controller = new FirstPersonCameraController(camera);
        Gdx.input.setInputProcessor(controller);
        controller.update();

        //test physics
        engine = new PhysicsEngine(course);
        iterator = engine.iterator();
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Update the camera
        controller.update();
        camera.update();

        // Render golf ball
        double height;
        if (iterator != null && iterator.hasNext()){
            currentState = iterator.next();
            LOGGER.log(Level.FINE, currentState.x() + " " + currentState.y());
            //System.out.println(state.x() + " " + state.y());
            height = 0.05 + expr.setVariable("x", currentState.x()).setVariable("y", currentState.y()).evaluate();
            golfBallInstance.transform.setTranslation((float) currentState.x(), (float) height, (float) currentState.y());
        } else if (iterator != null) {
            currentState = iterator.last();
            iterator = null;
        } else {
            gameLoop.renditionFinished();
            height = 0.05 + expr.setVariable("x", currentState.x()).setVariable("y", currentState.y()).evaluate();
            golfBallInstance.transform.setTranslation((float) currentState.x(), (float) height, (float) currentState.y());
        }

        // Render golf ball
        golfBallShadowBatch.begin(camera);
        golfBallShadowBatch.render(golfBallInstance);
        golfBallShadowBatch.end();
        golfBallBatch.begin(camera);
        golfBallBatch.render(golfBallInstance, environment);
        golfBallBatch.end();

        // Render target
        targetBatch.begin(camera);
        targetBatch.render(cylinderInstance, environment);
        targetBatch.render(poleInstance, environment);
        targetBatch.end();

        // render trees
        for(int i = 0; i < treeInstances.size(); i++) {
            treeShadowBatch.begin(camera);
            treeShadowBatch.render(treeInstances.get(i), environment);
            treeShadowBatch.end();
            treeBatch.begin(camera);
            treeBatch.render(treeInstances.get(i), environment);
            treeBatch.end();
            treeCrownShadowBatch.begin(camera);
            treeCrownShadowBatch.render(treeCrownInstances.get(i), environment);
            treeCrownShadowBatch.end();
            treeCrownBatch.begin(camera);
            treeCrownBatch.render(treeCrownInstances.get(i), environment);
            treeCrownBatch.end();
        }

        // Render shadows
        for(int i = 0; i < shadowBatches.length; i++)
            for(int j = 0; j < shadowBatches[0].length; j++) {
                shadowBatches[i][j].begin(camera);
                shadowBatches[i][j].render(terrainInstances[i][j]);
                shadowBatches[i][j].end();
            }

        // Render terrain
        for(int i = 0; i < modelBatches.length; i++)
            for(int j = 0; j < modelBatches[0].length; j++) {
                modelBatches[i][j].begin(camera);
                modelBatches[i][j].render(terrainInstances[i][j], environment);
                modelBatches[i][j].end();
            }

        // Render water
        waterBatch.begin(camera);
        waterBatch.render(waterInstance, environment);
        waterBatch.end();

        //System.out.println(engine.getState());

        // test input
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            gameLoop.shootBall(initialGuessBot.play(currentState));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            gameLoop.shootBall(simpleBot.play(currentState));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            gameLoop.shootBall(hillClimbingBot.play(currentState));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            gameLoop.shootBall(gradientDescent.play(currentState));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            gameLoop.shootBall(newtonRaphsonBot.play(currentState));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            gameLoop.shootBall(simulatedAnnealing.play(currentState));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            gameLoop.shootBall(humanPlayer.play(currentState));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
            gameLoop.shootBall(lineHillClimbingBot.play(currentState));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) {
            gameLoop.shootBall(randomRestartHillClimbingBot.play(currentState));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) {
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            System.out.println(engine.getState());
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            iterator = null;
            gameLoop.revertToLastValidState();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            gameLoop.restartCourse();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            manager.toMainStage();
        }
    }

    public void renderShot(PhysicsEngine.FrameRateIterator iterator) {
        this.iterator = iterator;
    }
    public void setCurrentState(StateVector4 state) {
        this.currentState = state;
    }

    @Override
    public void dispose() {
        for (int i = 0; i < modelBatches.length; i++)
            for(int j = 0; j < modelBatches[0].length; j++)
                modelBatches[i][j].dispose();
        for (ModelBatch[] shadowBatch : shadowBatches)
            for (ModelBatch aShadowBatch : shadowBatch)
                aShadowBatch.dispose();
        waterBatch.dispose();
        waterInstance.model.dispose();
        for (ModelInstance[] terrainInstance : terrainInstances)
            for (ModelInstance aTerrainInstance : terrainInstance)
                aTerrainInstance.model.dispose();
    }

    public Viewport getViewport() {
        return viewport;
    }
}
