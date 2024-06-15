package org.ken22.models;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import net.objecthunter.exp4j.Expression;

public class TerrainModel {
    private static float MESH_RESOLUTION = 0.2f;
    private static float BATCH_SIZE = 10;
    private float xMin, xMax, yMin, yMax;

    private Model terrainModel;

    private ModelBuilder[][] modelBuilders;
    private MeshPartBuilder[][] meshPartBuilders;
    private MeshPartBuilder[][] altMeshPartBuilders;
    private ModelInstance[][] terrainInstances;
    private ModelBatch[][] modelBatches;

    private ModelBatch[][] shadowBatches;

    private Expression expr;

    /**
     * Due to a vertex limit in the ModelBuilder class, the terrain is divided into multiple batches.
     *
     */
    public TerrainModel(Expression expr, float xMin, float xMax, float yMin, float yMax) {

        this.expr =  expr;

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
        altMeshPartBuilders = new MeshPartBuilder[modelBatches.length][modelBatches[0].length];
        terrainInstances = new ModelInstance[modelBatches.length][modelBatches[0].length];
        shadowBatches = new ModelBatch[modelBatches.length][modelBatches[0].length];

        for(int bi = 0; bi < modelBatches.length; bi++)
            for(int bj = 0; bj < modelBatches[0].length; bj++) {
                modelBuilders[bi][bj] = new ModelBuilder();
                modelBuilders[bi][bj].begin();
                meshPartBuilders[bi][bj] = modelBuilders[bi][bj].part("terrain", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, Materials.grassMaterial);
                altMeshPartBuilders[bi][bj] = modelBuilders[bi][bj].part("terrain", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, Materials.altGrassMaterial);
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
                        altMeshPartBuilders[bi][bj].triangle(p3, p2, p4);
                    }
                terrainModel = modelBuilders[bi][bj].end();
                terrainInstances[bi][bj] = new ModelInstance(terrainModel);
            }
    }

    public ModelInstance[][] getTerrainInstances() {
        return terrainInstances;
    }

    public ModelBatch[][] getModelBatches() {
        return modelBatches;
    }

    public ModelBatch[][] getShadowBatches() {
        return shadowBatches;
    }
}
