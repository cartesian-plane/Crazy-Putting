package org.ken22.models;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import net.objecthunter.exp4j.Expression;
import org.ken22.obstacles.SandPit;
import java.util.List;


public class TerrainModel {
    private static final float MESH_RESOLUTION = 0.2f; //MESH SIZE CHANGE FOR PERFORMANCE
    private static final float BATCH_SIZE = 8;
    private final float xMin, xMax, yMin, yMax;

    private Model terrainModel;



    private final ModelBuilder[][] modelBuilders;
    private final MeshPartBuilder[][] meshPartBuilders;
    private final MeshPartBuilder[][] altMeshPartBuilders;
    private final ModelInstance[][] terrainInstances;
    private final ModelBatch[][] modelBatches;
    private final ModelBatch[][] shadowBatches;



    private final Expression expr;
    private final List<SandPit> sandPits;

    /**
     * Due to a vertex limit in the ModelBuilder class, the terrain is divided into multiple batches.
     *
     */

    public TerrainModel(Expression expr, float xMin, float xMax, float yMin, float yMax, List<SandPit> sandPits) {
        this.expr = expr;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.sandPits = sandPits;

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

                //shadows
                shadowBatches[bi][bj] = new ModelBatch(new DepthShaderProvider());
                for (float i = xMin + bi * BATCH_SIZE; i < xMin + (bi + 1) * BATCH_SIZE; i += MESH_RESOLUTION)
                    for (float j = yMin + bj * BATCH_SIZE; j < yMin + (bj + 1) * BATCH_SIZE; j += MESH_RESOLUTION) {
                        if(i + MESH_RESOLUTION > xMax || j + MESH_RESOLUTION > yMax) //don't go past the bounds
                            continue;
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

                        Material material = isSand(x0, z0) || isSand(x1, z0) || isSand(x0, z1) || isSand(x1, z1) ? Materials.sandMaterial : Materials.grassMaterial;
                        MeshPartBuilder mpb = modelBuilders[bi][bj].part("terrain", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);
                        mpb.triangle(p1, p2, p3);

                        Material altMaterial = isSand(x0, z0) || isSand(x1, z0) || isSand(x0, z1) || isSand(x1, z1) ? Materials.sandMaterial : Materials.altGrassMaterial;
                        MeshPartBuilder mpb2 = modelBuilders[bi][bj].part("terrain_b", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, altMaterial);
                        mpb2.triangle(p3, p2, p4);
                    }
                terrainModel = modelBuilders[bi][bj].end();
                terrainInstances[bi][bj] = new ModelInstance(terrainModel);
            }
    }


    //check for sand
    private boolean isSand(float x, float y) {
        for (SandPit sandPit : sandPits) {
            double[] coordinates = sandPit.coordinates();
            float dx = (float) (x - coordinates[0]);
            float dy = (float) (y - coordinates[1]);
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance <= sandPit.radius()) {
                return true;
            }
        }
        return false;
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
