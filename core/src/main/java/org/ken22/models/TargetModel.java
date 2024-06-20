package org.ken22.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class TargetModel {
    private ModelInstance cylinderInstance;
    private ModelInstance poleInstance;

    public TargetModel(float radius) {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model cylinderModel = modelBuilder.createCylinder(radius, 0.05f, radius, 20,
            new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        cylinderInstance = new ModelInstance(cylinderModel);

        Model poleModel = modelBuilder.createCylinder(0.01f, 1f, 0.01f, 20,
            new Material(ColorAttribute.createDiffuse(Color.BROWN)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        poleInstance = new ModelInstance(poleModel);
    }

    public ModelInstance getCylinderInstance() {
        return cylinderInstance;
    }

    public ModelInstance getPoleInstance() {
        return poleInstance;
    }
}
