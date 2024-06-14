package org.ken22.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class GolfBallModel {
    private ModelInstance modelInstance;

    public GolfBallModel() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model sphereModel = modelBuilder.createSphere(0.1f, 0.1f, 0.1f, 20, 20,
            new Material(ColorAttribute.createDiffuse(Color.WHITE)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelInstance = new ModelInstance(sphereModel);
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }
}
