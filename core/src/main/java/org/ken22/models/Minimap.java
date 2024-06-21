package org.ken22.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.obstacles.SandPit;
import org.ken22.obstacles.Tree;
import org.ken22.screens.GolfScreen;
import org.ken22.utils.GolfExpression;
import org.ken22.utils.MathUtils;

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

    private double[][] heightMap = new double[WIDTH][HEIGHT];
    private boolean[][] waterMask = new boolean[WIDTH][HEIGHT];

    public Minimap(GolfCourse course) {
        this.course = course;
        this.expr = GolfExpression.expr(course);

        this.xMin = (float) (course.ballX() < course.targetXcoord() ? course.ballX() - GolfScreen.PADDING_SIZE  : course.targetXcoord()) - GolfScreen.PADDING_SIZE;
        this.xMax = (float) (course.ballX() > course.targetXcoord() ? course.ballX() + GolfScreen.PADDING_SIZE : course.targetXcoord()) + GolfScreen.PADDING_SIZE;
        this.yMin = (float) (course.ballY() > course.targetYcoord() ? course.targetYcoord() - GolfScreen.PADDING_SIZE : course.ballY()) - GolfScreen.PADDING_SIZE;
        this.yMax = (float) (course.ballY() < course.targetYcoord() ? course.targetYcoord() + GolfScreen.PADDING_SIZE: course.ballY()) + GolfScreen.PADDING_SIZE;

        init();
    }

    //initializing
    public void init() {
        terrainmap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGB888);
        initHeightMap(expr);

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                Color color;
                    if(waterMask[i][j])
                        color = Color.BLUE;
                    else
                        color = new Color(0f, (float) heightMap[i][j], 0f, 1); //takes normalized values to [0, 1]

                terrainmap.setColor(color);
                terrainmap.drawPixel(i, j);
            }
        }

        texture = new Texture(terrainmap);
        image = new Image(new TextureRegionDrawable(texture));
    }

    //updating state of minimap
    public  void update() {
        if (treemap != null) treemap.dispose();
        treemap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
        for (Tree tree : course.trees) {
            int i = projectX((float) tree.coordinates()[0]);
            int j = projectY((float) tree.coordinates()[1]);
            treemap.setColor(Color.BROWN);
            treemap.fillCircle(i, j, (int) tree.radius());
        }

        if (sandmap != null) sandmap.dispose();
        sandmap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
        for (SandPit sandPit : course.sandPits) {
            int i = projectX((float) sandPit.coordinates()[0]);
            int j = projectY((float) sandPit.coordinates()[1]);
            sandmap.setColor(Color.WHITE);
            sandmap.fillCircle(i, j, (int) sandPit.radius());
        }

        if (minimap != null) minimap.dispose();
        minimap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
        minimap.drawPixmap(terrainmap, 0, 0);
        minimap.drawPixmap(treemap, 0, 0);
        minimap.drawPixmap(sandmap, 0, 0);

        texture = new Texture(minimap);
        image.setDrawable(new TextureRegionDrawable(texture));
    }


    //creating base map height
    private void initHeightMap(Expression heightFunction) {
        double[] xCoords = MathUtils.linspace(xMin, xMax, WIDTH);
        double[] yCoords = MathUtils.linspace(yMin, yMax, HEIGHT);

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                // Set the values before evaluating the function
                heightFunction
                    .setVariable("x", xCoords[i])
                    .setVariable("y", yCoords[j]);

                heightMap[i][j] = heightFunction.evaluate();
                if (heightMap[i][j] < 0) waterMask[i][j] = true; // water at (i, j)
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
    }


    //projecting stuff onto minimap
    public int projectX(float x) {
        return (int) ((x - xMin) / (xMax - xMin) * WIDTH);
    }

    public int projectY(float y) {
        return (int) ((y - yMin) / (yMax - yMin) * HEIGHT);
    }

    public float unprojectX(int i) {
        return xMin + ((float) i)  / WIDTH * (xMax - xMin);
    }

    public float unprojectY(int j) {
        return yMin + ((float) j)  / HEIGHT * (yMax - yMin);
    }
}
