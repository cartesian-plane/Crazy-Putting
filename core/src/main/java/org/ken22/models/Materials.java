package org.ken22.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

public class Materials {
        public static Material grassMaterial = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        public static Material altGrassMaterial = new Material(ColorAttribute.createDiffuse(Color.FOREST));
        public static Material waterMaterial = new Material(ColorAttribute.createDiffuse(Color.BLUE));
        public static Material sandMaterial = new Material(ColorAttribute.createDiffuse(Color.YELLOW));
        public static Material treeMaterial = new Material(ColorAttribute.createDiffuse(Color.BROWN));
        public static Material wallMaterial = new Material(ColorAttribute.createDiffuse(Color.BLACK));

        public static Material sandTextureMaterial = new Material(TextureAttribute.createDiffuse(new Texture("sand.png")));
        public static Material grassTextureMaterial = new Material(TextureAttribute.createDiffuse(new Texture("grass.png")));
}
