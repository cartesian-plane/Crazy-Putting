package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
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
import java.util.ArrayList;
import java.util.Vector;

public class GolfScreen extends ScreenAdapter {

    private PerspectiveCamera camera;
    private FirstPersonCameraController controller;
    private Model cubeModel;
    private Model terrainModel;

    private ModelBuilder[][] modelBuilders;
    private MeshPartBuilder[][] meshPartBuilders;
    private ModelInstance[][] terrainInstances;
    private ModelBatch[][] modelBatches;

    private ModelBuilder waterBuilder;
    private MeshPartBuilder waterMeshPartBuilder;
    private ModelInstance waterInstance;
    private ModelBatch waterBatch;

    private Environment environment;
    private ModelBatch[][] shadowBatches;
    private ModelBatch waterShadowBatch;

    private GolfCourse course;
    private Expression expr;

    private float xMin, xMax, yMin, yMax;
    private float PADDING_SIZE = 10f;
    private float MESH_RESOLUTION = 0.1f;
    private float BATCH_SIZE = 10;



    /**
     * Everything in GolfScreen is initialized here, rather than in the show() method.
     * This is because the show() method is only called when the screen is set as the current screen
     * in the Game class, which is not the case here.
     */
    public GolfScreen(GolfCourse course) {
        this.course = course;
        expr = GolfExpression.expr(course);

        // Set map limits
        xMin = (float) (course.ballX() > course.targetXcoord() ? course.targetXcoord() -  PADDING_SIZE : course.ballX() - PADDING_SIZE);
        xMax = (float) (course.ballX() < course.targetXcoord() ? course.targetXcoord() +  PADDING_SIZE : course.ballX() + PADDING_SIZE);
        yMin = (float) (course.ballY() > course.targetYcoord() ? course.targetYcoord() -  PADDING_SIZE : course.ballY() - PADDING_SIZE);
        yMax = (float) (course.ballY() < course.targetYcoord() ? course.targetYcoord() +  PADDING_SIZE : course.ballY() + PADDING_SIZE);

        environment = new Environment();
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // Create terrain models
        modelBatches = new ModelBatch[(int) Math.ceil((xMax - xMin) / BATCH_SIZE)][(int) Math.ceil((yMax - yMin) / BATCH_SIZE)];
        for(int i = 0; i < modelBatches.length; i++) {
            for(int j = 0; j < modelBatches[0].length; j++) {
                modelBatches[i][j] = new ModelBatch();
            }
        }
        modelBuilders = new ModelBuilder[modelBatches.length][modelBatches[0].length];
        for(int i = 0; i < modelBuilders.length; i++) {
            for(int j = 0; j < modelBuilders[0].length; j++) {
                modelBuilders[i][j] = new ModelBuilder();
            }
        }
        meshPartBuilders = new MeshPartBuilder[modelBatches.length][modelBatches[0].length];
        terrainInstances = new ModelInstance[modelBatches.length][modelBatches[0].length];
        shadowBatches = new ModelBatch[modelBatches.length][modelBatches[0].length];

        Material textureMaterial = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        Material waterMaterial = new Material(ColorAttribute.createDiffuse(Color.BLUE));
        Material sandMaterial = new Material(ColorAttribute.createDiffuse(Color.YELLOW));
        Material treeMaterial = new Material(ColorAttribute.createDiffuse(Color.BROWN));

        for(int bi = 0; bi < modelBatches.length; bi++)
            for(int bj = 0; bj < modelBatches[0].length; bj++) {
                modelBuilders[bi][bj] = new ModelBuilder();
                modelBuilders[bi][bj].begin();
                meshPartBuilders[bi][bj] = modelBuilders[bi][bj].part("terrain", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, textureMaterial);
                //shadows
                shadowBatches[bi][bj] = new ModelBatch(new DepthShaderProvider());
                for (float i = xMin + bi * BATCH_SIZE; i < xMin + (bi + 1) * BATCH_SIZE; i += MESH_RESOLUTION)
                    for (float j = yMin + bj * BATCH_SIZE; j < yMin + (bj + 1) * BATCH_SIZE; j += MESH_RESOLUTION) {
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

                        meshPartBuilders[bi][bj].triangle(p1, p2, p3);
                        meshPartBuilders[bi][bj].triangle(p3, p2, p4);
                    }
                terrainModel = modelBuilders[bi][bj].end();
                terrainInstances[bi][bj] = new ModelInstance(terrainModel);
            }

        // Create water model
        waterBuilder = new ModelBuilder();
        waterBuilder.begin();
        waterMeshPartBuilder = waterBuilder.part("water", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, waterMaterial);
        //TODO: figure out what's wrong with the coordinates.
        Vector3 p1 = new Vector3(xMin, 0, yMin);
        Vector3 p2 = new Vector3(xMin, 0, yMax + 10f);
        Vector3 p3 = new Vector3(xMax + 10f, 0, yMin);
        Vector3 p4 = new Vector3(xMax+ 10f, 0, yMax + 10f);
        waterMeshPartBuilder.triangle(p1, p2, p3);
        waterMeshPartBuilder.triangle(p3, p2, p4);
        waterInstance = new ModelInstance(waterBuilder.end());
        waterBatch = new ModelBatch();


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
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Update the camera
        controller.update();
        camera.update();

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
        cubeModel.dispose();
        for (int i = 0; i < modelBatches.length; i++)
            for(int j = 0; j < modelBatches[0].length; j++)
                modelBatches[i][j].dispose();
        for (ModelBatch[] shadowBatch : shadowBatches)
            for (ModelBatch aShadowBatch : shadowBatch)
                aShadowBatch.dispose();
    }
}
