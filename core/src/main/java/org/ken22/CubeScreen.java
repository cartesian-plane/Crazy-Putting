package org.ken22;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.CourseParser;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.utils.GolfExpression;

import java.io.File;

public class CubeScreen implements Screen  {

    private PerspectiveCamera camera;
    private FirstPersonCameraController controller;
    private Model cubeModel;
    private Model terrainModel;
    private ModelBuilder modelBuilder;
    private MeshPartBuilder meshPartBuilder;
    private ModelInstance cubeInstance;
    private ModelBatch modelBatch;

    private Environment environment;
    private ModelBatch shadowBatch;

    private GolfCourse course;
    private Expression expr;

    private float MESH_RESOLUTION = 0.1f;

    @Override
    public void show() {
        CourseParser parser = new CourseParser(new File("input/sin(x)sin(y).json"));
        course = parser.getCourse();
        expr = GolfExpression.expr(course);

        environment = new Environment();
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        this.modelBatch = new ModelBatch();
        this.shadowBatch = new ModelBatch(new DepthShaderProvider());

        // Create a cube
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        Material textureMaterial = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        this.meshPartBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, textureMaterial);

        for(float i = 0; i < 5f; i += MESH_RESOLUTION)
            for (float j = 0; j < 5f; j += MESH_RESOLUTION) {
                float x0 = i;
                float z0 = j;
                float x1 = i + MESH_RESOLUTION;
                float z1 = j + MESH_RESOLUTION;

                float y00 = (float) expr.setVariable("x", x0).setVariable("y", z0).evaluate();
                float y01 = (float) expr.setVariable("x", x0).setVariable("y", z1).evaluate();
                float y10 = (float) expr.setVariable("x", x1).setVariable("y", z0).evaluate();
                float y11 = (float) expr.setVariable("x", x1).setVariable("y", z1).evaluate();

                Vector3 p1 = new Vector3(x0, y00, z0);
                Vector3 p2 = new Vector3(x0, y01, z1);
                Vector3 p3 = new Vector3(x1, y10, z0);
                Vector3 p4 = new Vector3(x1, y11, z1);

                meshPartBuilder.triangle(p1, p2, p3);
                meshPartBuilder.triangle(p3, p2, p4);
            }

        cubeModel = modelBuilder.end();
        cubeInstance = new ModelInstance(cubeModel);

        // Initialize the camera
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 0f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        // Initialize the FirstPersonCameraController
        controller = new FirstPersonCameraController(camera);
        Gdx.input.setInputProcessor(controller);
        controller.update();
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Update the camera
        controller.update();
        camera.update();

        // Render the shadow
        shadowBatch.begin(camera);
        shadowBatch.render(cubeInstance);
        shadowBatch.end();

        // Render the cube
        modelBatch.begin(camera);
        modelBatch.render(cubeInstance, environment);
        modelBatch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        cubeModel.dispose();
        modelBatch.dispose();
        shadowBatch.dispose();
    }
}
