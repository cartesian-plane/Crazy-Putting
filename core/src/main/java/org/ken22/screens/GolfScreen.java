package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.models.*;
import org.ken22.physics.engine.PhysicsEngine;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.SimplePlanarApproximationBot;
import org.ken22.utils.GolfExpression;

public class GolfScreen extends ScreenAdapter {
    private static float PADDING_SIZE = 10f;
    private float xMin, xMax, yMin, yMax;

    private PerspectiveCamera camera;
    private FirstPersonCameraController controller;

    private ModelBatch[][] modelBatches;
    private ModelBatch[][] shadowBatches;
    private ModelInstance[][] terrainInstances;

    private ModelBuilder waterBuilder;
    private MeshPartBuilder waterMeshPartBuilder;
    private ModelInstance waterInstance;
    private ModelBatch waterBatch;

    private ModelInstance golfBallInstance;
    private ModelBatch golfBallBatch;

    private ModelInstance targetInstance;
    private ModelBatch targetBatch;

    private Environment environment;

    PhysicsEngine.FrameRateIterator iterator;

    private GolfCourse course;
    private Expression expr;

    /**
     * Everything in GolfScreen is initialized here, rather than in the show() method.
     * This is because the show() method is only called when the screen is set as the current screen
     * in the Game class, which is not the case here.
     */
    public GolfScreen(GolfCourse course) {
        this.course = course;
        this.expr = GolfExpression.expr(course);

        // Set map limits
        xMin = (float) (course.ballX() > course.targetXcoord() ? course.targetXcoord() -  PADDING_SIZE : course.ballX() - PADDING_SIZE);
        xMax = (float) (course.ballX() < course.targetXcoord() ? course.targetXcoord() +  PADDING_SIZE : course.ballX() + PADDING_SIZE);
        yMin = (float) (course.ballY() > course.targetYcoord() ? course.targetYcoord() -  PADDING_SIZE : course.ballY() - PADDING_SIZE);
        yMax = (float) (course.ballY() < course.targetYcoord() ? course.targetYcoord() +  PADDING_SIZE : course.ballY() + PADDING_SIZE);

        // Initialize the environment
        environment = new Environment();
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // create terrain model
        TerrainModel terrainModel= new TerrainModel(expr, xMin, xMax, yMin, yMax);
        modelBatches = terrainModel.getModelBatches();
        shadowBatches = terrainModel.getShadowBatches();
        terrainInstances = terrainModel.getTerrainInstances();

        // Create water model
        WaterModel waterModel = new WaterModel(-0.005f, xMin, xMax, yMin, yMax);
        waterInstance = waterModel.getWaterInstance();
        waterBatch = waterModel.getWaterBatch();

        // Create golf ball model
        GolfBallModel golfBallModel = new GolfBallModel();
        golfBallInstance = golfBallModel.getModelInstance();
        golfBallBatch = new ModelBatch();
        golfBallInstance.transform.setTranslation((float) course.ballX(), 0f, (float) course.ballY());

        // Create target model
        TargetModel targetModel = new TargetModel((float) course.targetRadius());
        targetInstance = targetModel.getModelInstance();
        targetBatch = new ModelBatch();
        targetInstance.transform.setTranslation((float) course.targetXcoord(), 0f, (float) course.targetYcoord());

        // Initialize the camera
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set((float)course.ballX(), 0f, (float)course.ballY());
        camera.lookAt(1f, 0f, 1f);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        // Initialize the FirstPersonCameraController
        controller = new FirstPersonCameraController(camera);
        Gdx.input.setInputProcessor(controller);
        controller.update();

        //test physics
        PhysicsEngine engine = new PhysicsEngine(course);
        var bot = new SimplePlanarApproximationBot();
        engine.setState(bot.play(engine.getState(), course));
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
        StateVector4 state = iterator.next();
        var height = 0.05 + expr.setVariable("x", state.x()).setVariable("y", state.y()).evaluate();
        golfBallInstance.transform.setTranslation((float) state.x(), (float) height, (float) state.y());
        golfBallBatch.begin(camera);
        golfBallBatch.render(golfBallInstance, environment);
        golfBallBatch.end();

        // Render target
        targetBatch.begin(camera);
        targetBatch.render(targetInstance, environment);
        targetBatch.end();

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
}
