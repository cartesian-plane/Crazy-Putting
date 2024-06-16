package org.ken22.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.CourseParser;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.utils.MathUtils;

import java.io.File;

public class Minimap {
    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;

    private Pixmap pixmap;
    private Texture texture;
    private Image image;

    private float xMin, xMax, yMin, yMax;
    private GolfCourse course = new CourseParser(new File("input/sin(x)sin(y).json")).getCourse();
    private Expression expr;

    public Minimap(Expression expr) {
        this.expr = expr;

        this.xMin = (float) (course.ballX() < course.targetXcoord() ? course.ballX() : course.targetXcoord());
        this.xMax = (float) (course.ballX() > course.targetXcoord() ? course.ballX() : course.targetXcoord());
        this.yMin = (float) (course.ballY() > course.targetYcoord() ? course.targetYcoord() : course.ballY());
        this.yMax = (float) (course.ballY() < course.targetYcoord() ? course.targetYcoord() : course.ballY());

        System.out.println("xMax = " + xMax);
        System.out.println("xMin = " + xMin);
        System.out.println("yMax = " + yMax);
        System.out.println("yMin = " + yMin);

        // height for pixel (i, j) //includint projection into pixels
        // expr.setVariable("x", xMin + (xMax - xMin) * i  / WIDTH)
        //     .setVariable("y", yMin + (yMax - yMin) * j / HEIGHT).evaluate();
    }

    public Pixmap pixmap() {

        Pixmap pixmap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGB888);

        double[][] heightMap = heightMap(expr);

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                double gray = heightMap[i][j];
                Color color = new Color(0f, (float) gray, 0f, 1); //takes normalized values to [0, 1]

                pixmap.setColor(color);
                pixmap.drawPixel(i, j);
            }
        }

        return pixmap;
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
