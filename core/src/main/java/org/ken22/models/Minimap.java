package org.ken22.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.CourseParser;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.obstacles.Tree;
import org.ken22.utils.GolfExpression;
import org.ken22.utils.MathUtils;

import java.io.File;

public class Minimap {
    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;

    public Pixmap minimap;
    private Pixmap terrainmap;
    private Pixmap treemap;
    private Pixmap sandmap;

    public Texture texture;
    public Image image;

    private float xMin, xMax, yMin, yMax;
    private GolfCourse course;
    private Expression expr;

    public Minimap(GolfCourse course) {
        this.course = course;
        this.expr = GolfExpression.expr(course);

        this.xMin = (float) (course.ballX() < course.targetXcoord() ? course.ballX() : course.targetXcoord());
        this.xMax = (float) (course.ballX() > course.targetXcoord() ? course.ballX() : course.targetXcoord());
        this.yMin = (float) (course.ballY() > course.targetYcoord() ? course.targetYcoord() : course.ballY());
        this.yMax = (float) (course.ballY() < course.targetYcoord() ? course.targetYcoord() : course.ballY());

        init();
    }

    public void init() {
        terrainmap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGB888);
        double[][] heightMap = heightMap(expr);

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                double gray = heightMap[i][j];
                Color color = new Color(0f, (float) gray, 0f, 1); //takes normalized values to [0, 1]

                terrainmap.setColor(color);
                terrainmap.drawPixel(i, j);
            }
        }

        texture = new Texture(terrainmap);
        image = new Image(new TextureRegionDrawable(texture));
    }

    public  void update() {
        if (treemap != null) treemap.dispose();
        treemap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGB888);
        for (Tree tree : course.trees) {
            int i = project((float) tree.coordinates()[0]);
            int j = project((float) tree.coordinates()[1]);
            treemap.setColor(Color.BROWN);
            treemap.fillCircle(i, j, (int) tree.radius());
        }

        if(sandmap != null) sandmap.dispose();
        sandmap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGB888);

        if(minimap != null) minimap.dispose();
        minimap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGB888);
        for(int i = 0; i < WIDTH; i++) {
            for(int j = 0; j < HEIGHT; j++) {
                Color color;
                if (new Color(terrainmap.getPixel(i, j)).equals(Color.BROWN)) {
                    minimap.drawPixel(i, j, Color.BROWN.toIntBits());
                    color = Color.BROWN;
                } else {
                    minimap.drawPixel(i, j, terrainmap.getPixel(i, j));
                    color = new Color(terrainmap.getPixel(i, j));
                }

                minimap.setColor(color);
                minimap.drawPixel(i, j);
            }
        }

        texture = new Texture(minimap);
        image.setDrawable(new TextureRegionDrawable(texture));
    }

    private double[][] heightMap(Expression heightFunction) {
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

        // Normalize the heightmap to [0, 1]
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                heightMap[i][j] = (heightMap[i][j] - min) / (max - min);
            }
        }

        return heightMap;
    }

    public int project(float x) {
        return (int) ((x - xMin) / (xMax - xMin) * WIDTH);
    }

    public float unproject(int i) {
        return xMin + ((float) i)  / WIDTH * (xMax - xMin);
    }
}
