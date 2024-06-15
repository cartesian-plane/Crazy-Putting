package org.ken22.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.objecthunter.exp4j.Expression;
import org.ken22.utils.MathUtils;

public class Minimap {
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;

    private Pixmap pixmap;
    private Texture texture;
    private Image image;

    private float xMin, xMax, yMin, yMax;
    private Expression expr;

    public Minimap(Expression expr) {
        this.expr = expr;

        this.xMin = (float) expr.setVariable("x", 0).setVariable("y", 0).evaluate();
        this.xMax = (float) expr.setVariable("x", WIDTH).setVariable("y", 0).evaluate();
        this.yMin = (float) expr.setVariable("x", 0).setVariable("y", 0).evaluate();
        this.yMax = (float) expr.setVariable("x", 0).setVariable("y", HEIGHT).evaluate();

        System.out.println("xMax = " + xMax);
        System.out.println("xMin = " + xMin);
        System.out.println("yMax = " + yMax);
        System.out.println("yMin = " + yMin);

        // height for pixel (i, j) //includint projection into pixels
        // expr.setVariable("x", xMin + (xMax - xMin) * i  / WIDTH)
        //     .setVariable("y", yMin + (yMax - yMin) * j / HEIGHT).evaluate();
    }

    private double[][] generate(Expression heightFunction) {
        double[][] heightMap = new double[WIDTH][HEIGHT];
        double[] xCoords = MathUtils.linspace(xMin, xMax, WIDTH);
        double[] yCoords = MathUtils.linspace(yMin, yMax, HEIGHT);

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                // Set the values before evaluating the function
                heightFunction
                    .setVariable("x", xCoords[i])
                    .setVariable("y", yCoords[j]);

                heightMap[i][j] = heightFunction.evaluate();
            }
        }

        double min = MathUtils.min(heightMap);
        double max = MathUtils.max(heightMap);

        // Normalize the heightmap
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                heightMap[i][j] = (heightMap[i][j] - min) / (max - min);
            }
        }

        return heightMap;
    }

    public Pixmap createPixmapFromHaightMap() {

        Pixmap pixmap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGB888);

        double[][] heightMap = generate(expr);

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                double gray = heightMap[i][j] * 255;
                if (i==0 && j == 0) {
                    System.out.println("Heightmap[0][0]: " + heightMap[i][j]);
                }
                Color color = new Color((float) gray / 255, (float) gray / 255, (float) gray / 255, 1);

                pixmap.setColor(color);
                pixmap.drawPixel(i, j);
            }
        }

        texture = new Texture(pixmap);
        image = new Image(texture);
        return pixmap;
    }

    public void draw(SpriteBatch batch, float x, float y) {
        batch.draw(texture, x, y);
    }

    public int project(float x) {
        return (int) ((x - xMin) / (xMax - xMin) * WIDTH);
    }

    public float unproject(int i) {
        return xMin + ((float) i)  / WIDTH * (xMax - xMin);
    }
}
