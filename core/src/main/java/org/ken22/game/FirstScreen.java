
package org.ken22.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;
import org.ken22.physics.engine.UnrealEngine;
import org.ken22.physics.numerical_derivation.fivePointDifference;
import org.ken22.physics.numerical_integration.RK4;
import org.ken22.physics.system.PhysicsSystem;
import org.ken22.physics.vectors.GVec4;
import org.ken22.terrains.HeightMapTerrain;

import org.ken22.interfaces.IFunc;


import java.io.File;
import java.util.LinkedList;


/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private AssetManager manager;
    private boolean flash = false;
    private float flashTimer = 0;
    private ModelBatch modelBatch;
    private ModelInstance instance;
    private PerspectiveCamera cam;
    private Environment environment;
    private HeightMapTerrain heightMapTerrain;
    private HeightMapTerrain terrain;
    private Pixmap data = new Pixmap(Gdx.files.internal("heightmaps/pillowmap.png"));
    private Renderable ground;
    private SceneManager sceneManager;
    private SceneAsset sceneAsset;
    private Scene scene;
    private PerspectiveCamera camera;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private float time;
    private SceneSkybox skybox;
    private DirectionalLightEx light;
    private FirstPersonCameraController cameraController;
    private final LinkedList<Vector3> points = new LinkedList<>();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private float terrainHeight;
    private LinkedList<Vector3> positions = new LinkedList<>();

    // Physics stuff
    private UnrealEngine engine;




    private Scene terrainScene;
    // instance
    @Override
    public void show() {
        // Prepare your screen here.

        // create scene
        sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/avocado/Avocado.gltf"));
        scene = new Scene(sceneAsset.scene);
        scene.modelInstance.transform.setTranslation(0, 13f, 0);
        scene.modelInstance.transform.scl(10, 10, 10);



        SceneAsset waterAsset = new GLTFLoader().load(Gdx.files.internal("models/low_poly_-_puddle/puddle.gltf"));
        sceneManager = new SceneManager();
        sceneManager.addScene(scene);
        Scene waterScene = new Scene(waterAsset.scene);
        sceneManager.addScene(waterScene);
        Quaternion rotation = new Quaternion();
        scene.modelInstance.transform.getRotation(rotation);


        // Calculate the scale factor
        BoundingBox terrainBounds = new BoundingBox();
        scene.modelInstance.calculateBoundingBox(terrainBounds);
        Vector3 terrainSize = terrainBounds.getDimensions(new Vector3());
        System.out.println(terrainSize);
        BoundingBox waterBounds = new BoundingBox();
        waterScene.modelInstance.calculateBoundingBox(waterBounds);
        Vector3 waterSize = waterBounds.getDimensions(new Vector3());
        Vector3 scaleFactor = new Vector3(
            terrainSize.x / waterSize.x,
            terrainSize.y / waterSize.y,
            terrainSize.z / waterSize.z
        );

        System.out.println("scaleFactor = " + scaleFactor);
        waterScene.modelInstance.transform.scl(0.3f, 1f, 1f);
        waterScene.modelInstance.transform.rotate(Vector3.X, 63);
        waterScene.modelInstance.transform.translate(50, 0, 0);
        waterScene.modelInstance.transform.getRotation(rotation);

        float euler = rotation.getAngle();

// Print the Euler angles
        System.out.println("Rotation");
        System.out.println(euler);

        // setup camera (The BoomBox model is very small, so you may need to adapt camera settings for your scene)
        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        camera.position.set(10, 10, 10);
        camera.near = 1f;
        camera.far = 1000;
        sceneManager.setCamera(camera);
        camera.position.set(0,0, 0f);
        camera.lookAt(100, 0, 100);

        cameraController = new FirstPersonCameraController(camera);
        // change the camera controls sensitivity (check the FirstPersonCameraController class definition)
        cameraController.setDegreesPerPixel(0.25f);
        cameraController.setVelocity(12f);
        Gdx.input.setInputProcessor(cameraController);

        // setup light
        light = new DirectionalLightEx();
        light.direction.set(1, -3, 1).nor();
        light.color.set(Color.WHITE);
        sceneManager.environment.add(light);

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        // setup skybox
        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);
        createTerrain();


//
//        //setup physics engine  ALL OF tHIS WILL NEED to BE CHANGEd
//        IFunc<Double, Double> heightFunction = (vars) -> Math.sin((vars.get(0) - vars.get(1)) / 7) + 0.5; // height function
//        PhysicsEngine physicsEngine = new PhysicsEngine(0, 0, 0, 0, 0.01, 0, 10, 0.1, 0.2, 9.81, heightFunction);
//        //plugging things into physics engine


        Vector3 initialBallPosition = new Vector3(0, 0, 0);
        Vector3 targetPosition = new Vector3(100, 0, 0);
        double targetRadius = 5.0;
        float boundaryMinX = -10f, boundaryMinY = -10f, boundaryMaxX = 110f, boundaryMaxY = 10f;
        int maxStrokes = 10;

//        gameLoop = new GameLoop(physicsEngine, initialBallPosition, targetPosition, targetRadius, camera, boundaryMinX,
//            boundaryMinY, boundaryMaxX, boundaryMaxY, maxStrokes);

        //ALL OF tHIS WILL NEED to BE CHANGEd


        RK4 integrator = new RK4();
        fivePointDifference differentiator = new fivePointDifference();
        GVec4 initialState = new GVec4(0.0, 0.0, 1.0, 1.0, 1.0, 0.01);
        File coursejson = new File("input/golf-course.json");
        PhysicsSystem system = new PhysicsSystem(initialState,coursejson);
        engine = new UnrealEngine(system, integrator, differentiator);

    }

    private void createTerrain() {
        if (terrain != null) {
            terrain.dispose();
            sceneManager.removeScene(terrainScene);
        }

        terrain = new HeightMapTerrain(new Pixmap(Gdx.files.internal("heightmaps/expheightmap.png")), 20f);

        System.out.println(terrain.getHeightField().getPositionAt(new Vector3(0, 1, 0), 0, 0));
        terrainScene = new Scene(terrain.getModelInstance());
        sceneManager.addScene(terrainScene);
    }


    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.


        float deltaTime = Gdx.graphics.getDeltaTime();
        time += deltaTime;


        Vector3 sceneAssetPosition = new Vector3();
        scene.modelInstance.transform.getTranslation(sceneAssetPosition);
        positions.add(new Vector3(sceneAssetPosition));

        cameraController.update();
//        scene.modelInstance.transform.rotate(Vector3.Y, 10f * deltaTime);

        // arbitrary scaling factor
        // 1 game unit = 1m (meter)
        float scalingFactor = 9.560222f;
        Vector3 movementVector = new Vector3(0.016f / scalingFactor, 0, 0);
        // scene.modelInstance.transform.translate(movementVector);

        GVec4 temp = engine.nextStep();

        // Get the velocities from the state vector
        float vx = (float) temp.getVx0();
        float vy = (float) temp.getVy0();

        Vector3 nextStep = new Vector3(vx / scalingFactor / 60, 0, vy / scalingFactor / 60);
        scene.modelInstance.transform.translate(nextStep);



//        // animate camera
//        camera.position.setFromSpherical(MathUtils.PI/4, time * .3f).scl(.02f);
//        camera.up.set(Vector3.Y);
//        camera.lookAt(Vector3.Zero);
//        camera.update();

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            createTerrain();
        }
        // render

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        sceneManager.update(deltaTime);
        sceneManager.render();
//
//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.RED);
//        for (int i = 0; i < positions.size() - 1; i++) {
//            shapeRenderer.line(positions.get(i), positions.get(i + 1));
//        }
//        shapeRenderer.end();


        // System.out.println(time);
        if (sceneAssetPosition.x >= 100) {
            System.out.println("object reached x: " + sceneAssetPosition.x);
            throw new RuntimeException();
        }



    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        sceneManager.updateViewport(width, height);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        sceneManager.dispose();
        sceneAsset.dispose();
        environmentCubemap.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        brdfLUT.dispose();
        skybox.dispose();

    }
}
