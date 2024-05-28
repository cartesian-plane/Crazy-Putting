package org.ken22.graphics.terrains;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

public class HeightMapTerrain extends Terrain {

    private final HeightField heightField;

    public HeightMapTerrain(Pixmap data, float magnitude) {
        this.size = 100;
        this.width = data.getWidth();
        this.heightMagnitude = magnitude;

        heightField = new HeightField(true, data, true,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
                VertexAttributes.Usage.TextureCoordinates);
        data.dispose();
        heightField.corner00.set(-size / 2f, 0, -size / 2f);
        heightField.corner10.set(size / 2f, 0, -size / 2f);
        heightField.corner01.set(-size / 2f, 0, size / 2f);
        heightField.corner11.set(size / 2f, 0, size / 2f);
        //sets some default colors; keep this commented out
//        heightField.color00.set(0, 0, 1, 1);
//        heightField.color01.set(0, 1, 1, 1);
//        heightField.color10.set(1, 0, 1, 1);
//        heightField.color11.set(1, 1, 1, 1);
        heightField.magnitude.set(0f, magnitude, 0f);
        heightField.update();


        Texture texture = new Texture(Gdx.files.internal("textures/wispy-grass-meadow_albedo.png"), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        PBRTextureAttribute textureAttribute = PBRTextureAttribute.createBaseColorTexture(texture);
        textureAttribute.scaleU = 10f;
        textureAttribute.scaleV = 10f;

        Material material = new Material();
        material.set(textureAttribute);

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.part("terrain", heightField.mesh, GL20.GL_TRIANGLES, material);
        modelInstance = new ModelInstance(modelBuilder.end());


    }

    public HeightField getHeightField() {
        return heightField;
    }

    @Override
    public void dispose() {
        heightField.dispose();
    }
}
