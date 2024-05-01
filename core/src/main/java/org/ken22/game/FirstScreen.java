package org.ken22.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.ScreenUtils;
import org.ken22.terrains.HeightMapTerrain;
import org.ken22.terrains.Terrain;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private AssetManager manager;
    private boolean flash = false;
    private float flashTimer = 0;
    private ModelBatch modelBatch;
    private ModelInstance instance;
    private PerspectiveCamera cam;
    private CameraInputController camController;
    private Environment environment;
    private HeightMapTerrain heightMapTerrain;
    private Terrain terrain;
    private Pixmap data = new Pixmap(Gdx.files.internal("heightmaps/pillowmap.png"));
    private Renderable ground;
    // instance
    @Override
    public void show() {
        // Prepare your screen here.

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 1000f;
        cam.update();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

        modelBatch = new ModelBatch();

        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createSphere(
            10f, 10f, 10f,
            20, 20,
            new Material(ColorAttribute.createDiffuse(new Color(241f/255f, 194f/255f, 125f/255f, 1))),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        instance = new ModelInstance(model);

        HeightMapTerrain heightMapTerrain = new HeightMapTerrain(data, 60f);
        ground = new Renderable();
        ground.environment = environment;
        ground.meshPart.mesh = heightMapTerrain.getHeightField().mesh;
        ground.meshPart.primitiveType = GL20.GL_TRIANGLES;
        ground.meshPart.offset = 0;
        ground.meshPart.size = heightMapTerrain.getHeightField().mesh.getNumIndices();
        ground.meshPart.update();
        ground.material = new Material();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));



    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.

        camController.update();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instance, environment);
        modelBatch.render(ground);
        modelBatch.end();

    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
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
    }
}
