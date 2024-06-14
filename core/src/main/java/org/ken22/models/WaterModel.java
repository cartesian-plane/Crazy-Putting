package org.ken22.models;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class WaterModel {
    private ModelBuilder waterBuilder;
    private MeshPartBuilder waterMeshPartBuilder;
    private ModelInstance waterInstance;
    private ModelBatch waterBatch;

    //TODO: figure out what's wrong with the coordinates.
    public WaterModel(float xMin, float xMax, float yMin, float yMax) {
        this(0, xMin, xMax, yMin, yMax);
    }
    public WaterModel(float height, float xMin, float xMax, float yMin, float yMax) {

        waterBuilder = new ModelBuilder();
        waterBuilder.begin();
        waterMeshPartBuilder = waterBuilder.part("water", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, Materials.waterMaterial);

        Vector3 p1 = new Vector3(xMin, height, yMin);
        Vector3 p2 = new Vector3(xMin, height, yMax);
        Vector3 p3 = new Vector3(xMax, height, yMin);
        Vector3 p4 = new Vector3(xMax, height, yMax);

        waterMeshPartBuilder.triangle(p1, p2, p3);
        waterMeshPartBuilder.triangle(p3, p2, p4);

        waterInstance = new ModelInstance(waterBuilder.end());
        waterBatch = new ModelBatch();
    }

    public ModelInstance getWaterInstance() {
        return waterInstance;
    }

    public ModelBatch getWaterBatch() {
        return waterBatch;
    }
}
