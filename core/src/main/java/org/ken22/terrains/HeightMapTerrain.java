package org.ken22.terrains;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class HeightMapTerrain extends Terrain {

    private final HeightField heightField;

    public HeightMapTerrain(Pixmap data, float magnitude) {
        this.size = 200;
        this.width = data.getWidth();
        this.heightMagnitude = magnitude;

        heightField = new HeightField(true, data, true,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
                VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);
        data.dispose();
        heightField.corner00.set(-10f, 0, 0);
        heightField.corner10.set(size, 0, -10f);
        heightField.corner01.set(-10f, 0, size);
        heightField.corner11.set(size, 0, size);
        heightField.color00.set(0, 0, 1, 1);
        heightField.color01.set(0, 1, 1, 1);
        heightField.color10.set(1, 0, 1, 1);
        heightField.color11.set(1, 1, 1, 1);
        heightField.magnitude.set(0f, magnitude, 0f);
        heightField.update();

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.part("terrain", heightField.mesh, GL20.GL_TRIANGLES, new Material());
        modelInstance = new ModelInstance(modelBuilder.end());


    }

    public HeightField getHeightField() {
        return heightField;
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not implemented yet.");

    }
}
