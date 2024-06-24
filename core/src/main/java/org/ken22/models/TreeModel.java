package org.ken22.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class TreeModel {
    private ModelInstance treeInstance;
    private ModelInstance crownInstance;
    private float height;

    public TreeModel(float radius) {
        ModelBuilder modelBuilder = new ModelBuilder();
        height = 3f + ((float)Math.random()) * 1.0f;

        // base
        Model treeModel = modelBuilder.createCylinder(radius, height, radius, 20,
            new Material(ColorAttribute.createDiffuse(Color.BROWN)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        //leaff
        float crownHeight = height / 2;
        float crownRadius = radius * 2;
        Model crownModel = modelBuilder.createCone(crownRadius, crownHeight, crownRadius, 20,
            new Material(ColorAttribute.createDiffuse(Color.FOREST)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        treeInstance = new ModelInstance(treeModel);
        crownInstance = new ModelInstance(crownModel);

        crownInstance.transform.setTranslation(0, height / 2 + crownHeight / 2, 0);
    }

    public ModelInstance getTreeInstance() {
        return treeInstance;
    }

    public ModelInstance getCrownInstance() {
        return crownInstance;
    }

    public float getHeight() {
        return height / 2;
    }
}
