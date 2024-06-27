package org.ken22.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import org.ken22.input.InjectedClass;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.models.*;
import org.ken22.obstacles.*;
import org.ken22.physics.engine.PhysicsEngine;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.bots.BotFactory;
import org.ken22.players.bots.newtonraphson.BasicNewtonRaphsonBot;
import org.ken22.players.bots.newtonraphson.NewtonRaphsonBot;
import org.ken22.players.bots.hillclimbing.SidewaysStepsHillCrimbingBot;
import org.ken22.players.bots.simulatedannealing.SimulatedAnnealing;
import org.ken22.players.bots.hillclimbing.HillClimbingBot;
import org.ken22.players.bots.hillclimbing.LineHillClimbingBot;
import org.ken22.players.bots.hillclimbing.RandomRestartHillClimbingBot;
import org.ken22.players.bots.simplebots.InitialGuessBot;
import org.ken22.players.bots.simplebots.SimplePlanarApproximationBot;
import org.ken22.stages.HumanPlayerDialogStage;

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
        LOGGER.setLevel(Level.WARNING); // (1)

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

    private ArrayList<ModelInstance> wallInstances;
    private ModelBatch wallBatch;

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
    private SidewaysStepsHillCrimbingBot sidewaysStepsHillCrimbingBot;
    private SimulatedAnnealing simulatedAnnealing;
    private BasicNewtonRaphsonBot basicNewtonRaphsonBot;

    private GameLoop gameLoop;

    private PhysicsEngine.FrameRateIterator iterator;
    private StateVector4 currentState;

    private GolfCourse course;
    private InjectedClass expr;

    private HumanPlayerDialogStage humanPlayerDialogStage;
    boolean humanPlayerDialogOpen = false;

    private Camera followingCamera;
    private InputProcessor followingController;
    private boolean following;

    private Camera currentCamera;

    private SpriteBatch spriteBatch = new SpriteBatch();
    private BitmapFont font = new BitmapFont();

    /**
     * Everything in GolfScreen is initialized here, rather than in the show() method.
     * This is because the show() method is only called when the screen is set as the current screen
     * in the Game class, which is not the case here.
     */
    public GolfScreen(ScreenManager manager, GolfCourse course, BotFactory botFactory, GameLoop gameLoop) {
        this.manager = manager;
        this.course = course;
        this.expr = course.getInjectedExpression();
        this.currentState = new StateVector4(course.ballX(), course.ballY(), 0, 0);
        this.botFactory = botFactory;
        this.gameLoop = gameLoop;
        gameLoop.setGolfScreen(this);
        this.humanPlayerDialogStage = new HumanPlayerDialogStage(this);

        printHelpMessage();
        initialGuessBot = botFactory.initialGuessBot(course);
        simpleBot = botFactory.planarApproximationBot(course);
        hillClimbingBot = botFactory.hillClimbingBot(course, initialGuessBot);
        lineHillClimbingBot = botFactory.lineHillClimbingBot(course, initialGuessBot);
        randomRestartHillClimbingBot = botFactory.randomRestartHillClimbingBot(course, initialGuessBot);
        sidewaysStepsHillCrimbingBot = botFactory.gradientDescent(course);
        newtonRaphsonBot = botFactory.newtonRaphsonBot(course, initialGuessBot);
        basicNewtonRaphsonBot = botFactory.basicNewtonRaphsonBot(course, initialGuessBot);
        simulatedAnnealing = botFactory.simulatedAnnealing(course);

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
        TerrainModel terrainModel = new TerrainModel(expr, xMin, xMax, yMin, yMax, course.getSandPits());
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
            var treeBaseHeight = (float) expr.evaluate(t.coordinates()[0], t.coordinates()[1]);
            treeInstance.transform.setTranslation((float) t.coordinates()[0], treeBaseHeight, (float) t.coordinates()[1]);
            treeInstances.add(treeInstance);
            var treeCrownInstance = treeModel.getCrownInstance();
            treeCrownInstance.transform.setTranslation((float) t.coordinates()[0], treeBaseHeight + treeModel.getHeight(), (float) t.coordinates()[1]);
            treeCrownInstances.add(treeCrownInstance);
        }

        // Create wall models
        wallInstances = new ArrayList<>();
        wallBatch = new ModelBatch();
        for (Wall w : course.walls) {
            WallModel wallModel = new WallModel(w, expr);
            wallInstances.add(wallModel.getWallInstance());
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

        var targetHeight =
            expr.evaluate(course.targetXcoord(), course.targetYcoord());
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

        //Initialize the following camera
        followingCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        followingCamera.position.set((float)course.ballX(), 0f, (float)course.ballY());
        followingCamera.lookAt(1f, 0f, 1f);
        followingCamera.near = 0.1f;
        followingCamera.far = 300f;
        followingCamera.update();
        //Create a viewport
        followingController = new FollowingCameraController(followingCamera);
        following = false;

        Gdx.input.setInputProcessor(controller);
        controller.update();

        currentCamera = camera;
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            // Render golf ball
            double height;
            if (iterator != null && iterator.hasNext()){
                currentState = iterator.next();
                LOGGER.log(Level.FINE, currentState.x() + " " + currentState.y());
                //System.out.println(state.x() + " " + state.y());
                height =
                    0.05 + expr.evaluate(currentState.x(), currentState.y());
                golfBallInstance.transform.setTranslation((float) currentState.x(), (float) height, (float) currentState.y());
            } else if (iterator != null) {
                currentState = iterator.last();
                iterator = null;
            } else {
                gameLoop.renditionFinished();
                height =
                    0.05 + expr.evaluate(currentState.x(), currentState.y());
                golfBallInstance.transform.setTranslation((float) currentState.x(), (float) height, (float) currentState.y());
            }

        if(!humanPlayerDialogOpen) {
            // Update the camera
            if(!following) {
                controller.update();
                camera.update();
            } else {
                double terrainLevel = expr.evaluate(currentState.x(), currentState.y());
                followingCamera.position.set(
                    (float) currentState.x(),
                    (float) terrainLevel + 0.5f,
                    (float) currentState.y());
                //followingCamera.lookAt((float) currentState.x(), (float) terrainLevel, (float) currentState.y());
                followingCamera.update();
            }

            // Render golf ball
            golfBallShadowBatch.begin(currentCamera);
            golfBallShadowBatch.render(golfBallInstance);
            golfBallShadowBatch.end();
            golfBallBatch.begin(currentCamera);
            golfBallBatch.render(golfBallInstance, environment);
            golfBallBatch.end();

            // Render target
            targetBatch.begin(currentCamera);
            targetBatch.render(cylinderInstance, environment);
            targetBatch.render(poleInstance, environment);
            targetBatch.end();

            // Render trees
            for (int i = 0; i < treeInstances.size(); i++) {
                treeShadowBatch.begin(currentCamera);
                treeShadowBatch.render(treeInstances.get(i), environment);
                treeShadowBatch.end();

                treeBatch.begin(currentCamera);
                treeBatch.render(treeInstances.get(i), environment);
                treeBatch.end();

                treeCrownShadowBatch.begin(currentCamera);
                treeCrownShadowBatch.render(treeCrownInstances.get(i), environment);
                treeCrownShadowBatch.end();

                treeCrownBatch.begin(currentCamera);
                treeCrownBatch.render(treeCrownInstances.get(i), environment);
                treeCrownBatch.end();
            }

            // Render walls
            wallBatch.begin(currentCamera);
            for (ModelInstance wallInstance : wallInstances) {
                wallBatch.render(wallInstance, environment);
            }
            wallBatch.end();



            // Render shadows
            for(int i = 0; i < shadowBatches.length; i++)
                for(int j = 0; j < shadowBatches[0].length; j++) {
                    shadowBatches[i][j].begin(currentCamera);
                    shadowBatches[i][j].render(terrainInstances[i][j]);
                    shadowBatches[i][j].end();
                }

            // Render terrain
            for(int i = 0; i < modelBatches.length; i++)
                for(int j = 0; j < modelBatches[0].length; j++) {
                    modelBatches[i][j].begin(currentCamera);
                    modelBatches[i][j].render(terrainInstances[i][j], environment);
                    modelBatches[i][j].end();
                }

            // Render water
            waterBatch.begin(currentCamera);
            waterBatch.render(waterInstance, environment);
            waterBatch.end();

            // text
            var startX = Gdx.graphics.getWidth() - 250f;
            var startY = Gdx.graphics.getHeight();
            var deltaY = -15f;
            this.spriteBatch.begin();
            this.font.setColor(Color.WHITE);
            this.font.draw(spriteBatch, "Ball coords: ("+currentState.x()+", "+currentState.y()+")", startX/2-100f, startY+deltaY);
            this.font.draw(spriteBatch, "P: human player", startX, startY + deltaY*3);
            this.font.draw(spriteBatch, "SPACE : initial guess bot", startX, startY + deltaY*4);
            this.font.draw(spriteBatch, "1 : simple rule-based bot", startX, startY + deltaY*5);
            this.font.draw(spriteBatch, "2 : simmulated annealing bot", startX, startY + deltaY*6);
            this.font.draw(spriteBatch, "3 : line search hill-climbing bot", startX, startY + deltaY*7);
            this.font.draw(spriteBatch, "4 : random restarts hill-climbing bot", startX, startY + deltaY*8);
            this.font.draw(spriteBatch, "5 : sideways steps hill-climbing bot", startX, startY + deltaY*9);
            this.font.draw(spriteBatch, "6 : simple hill-climbing bot", startX, startY + deltaY*10);
            this.font.draw(spriteBatch, "7 : newton-raphson bot (f'=0)", startX, startY + deltaY*11);
            this.font.draw(spriteBatch, "8 : basic newton-raphson bot (f=0) ", startX, startY + deltaY*12);
            this.font.draw(spriteBatch, "T : revert to last valid state", startX, startY + deltaY*14);
            this.font.draw(spriteBatch, "R : restart course", startX, startY + deltaY*15);
            this.font.draw(spriteBatch, "F : switch camera", startX, startY+deltaY*16);
            this.font.draw(spriteBatch, "K : print camera position", startX, startY + deltaY*17);

            this.spriteBatch.end();

            // test input
            if(Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                this.humanPlayerDialogOpen = true;
                Gdx.input.setInputProcessor(humanPlayerDialogStage);
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                gameLoop.shootBall(initialGuessBot.play(currentState));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                gameLoop.shootBall(simpleBot.play(currentState));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                gameLoop.shootBall(simulatedAnnealing.play(currentState));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                gameLoop.shootBall(lineHillClimbingBot.play(currentState));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
                gameLoop.shootBall(randomRestartHillClimbingBot.play(currentState));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
                gameLoop.shootBall(sidewaysStepsHillCrimbingBot.play(currentState));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
                gameLoop.shootBall(hillClimbingBot.play(currentState));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
                gameLoop.shootBall(newtonRaphsonBot.play(currentState));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) {
                gameLoop.shootBall(basicNewtonRaphsonBot.play(currentState));
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
                gameLoop.printState();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                iterator = null;
                gameLoop.revertToLastValidState();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                gameLoop.restartCourse();
            }
        } else {
            humanPlayerDialogStage.act();
            humanPlayerDialogStage.draw();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            following = !following;
            if(following) {
                currentCamera = followingCamera;
                viewport.setCamera(currentCamera);
                Gdx.input.setInputProcessor(followingController);
            } else {
                currentCamera = camera;
                viewport.setCamera(currentCamera);
                Gdx.input.setInputProcessor(controller);
            }
        }

         if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            manager.toMainStage();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            System.out.println("Camera Position: ("+ "x:" + camera.position.x + ", " + "y:" + currentCamera.position.y + ", " + "z:" + currentCamera.position.z + ")");
        }
    }

    public void renderShot(PhysicsEngine.FrameRateIterator iterator) {
        this.iterator = iterator;
    }
    public void setCurrentState(StateVector4 state) {
        this.currentState = state;
    }

    public void humanDialogAccepted() {
        humanPlayerDialogOpen = false;
        gameLoop.shootBall(humanPlayerDialogStage.getShot(currentState));
        Gdx.input.setInputProcessor(controller);
    }

    public void humanDialogRejected() {
        humanPlayerDialogOpen = false;
        Gdx.input.setInputProcessor(controller);
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

    public void printHelpMessage() {
        System.out.println("Shortcuts: ");
        System.out.println("Initial Guess Bot: SPACE");
        System.out.println("Simple Bot: NUM_1");
        System.out.println("Hill Climbing Bot: NUM_2");
//        System.out.println("Simple Bot: NUM_2");
//        System.out.println("Hill Climbing Bot: NUM_4");
        System.out.println("Gradient Descent: NUM_3");
        System.out.println("Newton Raphson Bot:  NUM_4");
//        System.out.println("Gradient Descent: NUM_6");
        System.out.println("Simulated Annealing: NUM_5");
//        System.out.println("Newton Raphson Bot: NUM_8");
        System.out.println("Human Player: NUM_6");
//        System.out.println("Simulated Annealing: NUM_10");
        System.out.println("Line Hill Climbing Bot: NUM_7");
        System.out.println("Random Restart Hill Climbing Bot: NUM_8");
//        System.out.println("Line Hill Climbing Bot: NUM_14");
        System.out.println("Basic Newton Raphson Bot: NUM_9");
    }
}
