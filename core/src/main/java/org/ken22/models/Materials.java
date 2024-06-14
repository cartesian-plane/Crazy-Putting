package org.ken22.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

public class Materials {
        public static Material grassMaterial = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        public static Material altGrassMaterial = new Material(ColorAttribute.createDiffuse(Color.FOREST));
        public static Material waterMaterial = new Material(ColorAttribute.createDiffuse(Color.BLUE));
        public static Material sandMaterial = new Material(ColorAttribute.createDiffuse(Color.YELLOW));
        public static Material treeMaterial = new Material(ColorAttribute.createDiffuse(Color.BROWN));
        public static Material wallMaterial = new Material(ColorAttribute.createDiffuse(Color.BLACK));
}
