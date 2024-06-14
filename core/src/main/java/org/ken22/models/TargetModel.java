package org.ken22.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class TargetModel {
    private ModelInstance modelInstance;

    public TargetModel(float radius) {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model cylinderModel = modelBuilder.createCylinder(radius, 2f, radius, 20,
            new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelInstance = new ModelInstance(cylinderModel);
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }
}
