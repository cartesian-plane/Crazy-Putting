package org.ken22.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.objecthunter.exp4j.Expression;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.obstacles.SandPit;
import org.ken22.obstacles.Tree;
import org.ken22.screens.GolfScreen;
import org.ken22.utils.MathUtils;
import org.ken22.obstacles.Wall;

public class Minimap {
    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;

    public Pixmap minimap;
    private Pixmap terrainmap;
    private Pixmap treemap;
    private Pixmap sandmap;
    private Pixmap wallmap;

    public Texture texture;
    public Image image;

    private float xMin, xMax, yMin, yMax;
    private GolfCourse course;
    private Expression expr;

    private double[][] heightMap = new double[WIDTH][HEIGHT];
    private boolean[][] waterMask = new boolean[WIDTH][HEIGHT];

    public Minimap(GolfCourse course, Viewport viewport) {
        this.course = course;
        this.expr = course.expression;

        float padding = GolfScreen.PADDING_SIZE;

        // initial boundaries
        this.xMin = (float) Math.min(course.ballX(), course.targetXcoord()) - padding;
        this.xMax = (float) Math.max(course.ballX(), course.targetXcoord()) + padding;
        this.yMin = (float) Math.min(course.ballY(), course.targetYcoord()) - padding;
        this.yMax = (float) Math.max(course.ballY(), course.targetYcoord()) + padding;

        // this important
        adjustRange();

        init();
    }



    private void adjustRange() {
        float range = (float) course.range();
        float xRange = xMax - xMin;
        float yRange = yMax - yMin;

        if (xRange < range || yRange < range) {
            float maxRange = Math.max(xRange, yRange);

            if (xRange < maxRange) {
                float centerX = (xMin + xMax) / 2;
                xMin = centerX - maxRange / 2;
                xMax = centerX + maxRange / 2;
            }

            if (yRange < maxRange) {
                float centerY = (yMin + yMax) / 2;
                yMin = centerY - maxRange / 2;
                yMax = centerY + maxRange / 2;
            }
        }
    }



    public void init() {
        terrainmap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGB888);
        initHeightMap(expr);

        double min = MathUtils.min(heightMap);
        double max = MathUtils.max(heightMap);

        if (min == max) {
            terrainmap.setColor(Color.GREEN);
            terrainmap.fill();
        } else {
            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    Color color;
                    if (waterMask[i][j])
                        color = Color.BLUE;
                    else
                        color = new Color(0f, (float) heightMap[i][j], 0f, 1); //takes normalized values to [0, 1]

                    terrainmap.setColor(color);
                    terrainmap.drawPixel(i, j);
                }
            }
        }

        // tart position
        int startX = projectX((float) course.ballX());
        int startY = projectY((float) course.ballY());
        terrainmap.setColor(Color.PURPLE);
        terrainmap.fillCircle(startX, startY, 5);

        // target position
        int targetX = projectX((float) course.targetXcoord());
        int targetY = projectY((float) course.targetYcoord());
        terrainmap.setColor(Color.RED);
        terrainmap.fillCircle(targetX, targetY, 5);



        texture = new Texture(terrainmap);
        image = new Image(new TextureRegionDrawable(texture));
    }

    public void update() {
        if (treemap != null) treemap.dispose();
        treemap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
        for (Tree tree : course.trees) {
            int i = projectX((float) tree.coordinates()[0]);
            int j = projectY((float) tree.coordinates()[1]);
            treemap.setColor(Color.BROWN);
            treemap.fillCircle(i, j, scaleToMap(tree.radius()));
        }

        if (sandmap != null) sandmap.dispose();
        sandmap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
        for (SandPit sandPit : course.sandPits) {
            int i = projectX((float) sandPit.coordinates()[0]);
            int j = projectY((float) sandPit.coordinates()[1]);
            sandmap.setColor(Color.WHITE);
            sandmap.fillCircle(i, j, scaleToMap(sandPit.radius()));
        }

        if (wallmap != null) wallmap.dispose();
        wallmap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
        for (Wall wall : course.walls) {
            int startX = projectX((float) wall.startPoint()[0]);
            int startY = projectY((float) wall.startPoint()[1]);
            int endX = projectX((float) wall.endPoint()[0]);
            int endY = projectY((float) wall.endPoint()[1]);
            drawThickLine(wallmap, startX, startY, endX, endY, scaleToMap(wall.thickness()), Color.PINK);
        }

        if (minimap != null) minimap.dispose();
        minimap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
        minimap.drawPixmap(terrainmap, 0, 0);
        minimap.drawPixmap(treemap, 0, 0);
        minimap.drawPixmap(sandmap, 0, 0);
        minimap.drawPixmap(wallmap, 0, 0);

        texture = new Texture(minimap);
        image.setDrawable(new TextureRegionDrawable(texture));
    }

    private void initHeightMap(Expression heightFunction) {
        double[] xCoords = MathUtils.linspace(xMin, xMax, WIDTH);
        double[] yCoords = MathUtils.linspace(yMin, yMax, HEIGHT);

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                heightFunction
                    .setVariable("x", xCoords[i])
                    .setVariable("y", yCoords[j]);

                heightMap[i][j] = heightFunction.evaluate();
                if (heightMap[i][j] < 0) waterMask[i][j] = true; // water at (i, j)
            }
        }

        double min = MathUtils.min(heightMap);
        double max = MathUtils.max(heightMap);

        if (min != max) {
            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    heightMap[i][j] = (heightMap[i][j] - min) / (max - min);
                }
            }
        }
    }

    private int scaleToMap(double value) {
        double scale = Math.max(xMax - xMin, yMax - yMin);
        return (int) (value / scale * WIDTH);
    }

    public int projectX(float x) {
        return (int) ((x - xMin) / (xMax - xMin) * WIDTH);
    }

    public int projectY(float y) {
        return (int) ((y - yMin) / (yMax - yMin) * HEIGHT);
    }

    public float unprojectX(int i) {
        return xMin + ((float) i) / WIDTH * (xMax - xMin);
    }

    public float unprojectY(int j) {
        return yMin + ((float) j) / HEIGHT * (yMax - yMin);
    }


    private void drawThickLine(Pixmap pixmap, int x1, int y1, int x2, int y2, int thickness, Color color) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int halfThickness = thickness / 2;

        int dx = (int) (halfThickness * Math.sin(angle));
        int dy = (int) (halfThickness * Math.cos(angle));

        int[] xPoints = {x1 - dx, x1 + dx, x2 + dx, x2 - dx};
        int[] yPoints = {y1 + dy, y1 - dy, y2 - dy, y2 + dy};

        pixmap.setColor(color);
        fillPolygon(pixmap, xPoints, yPoints);
    }

    private void fillPolygon(Pixmap pixmap, int[] xPoints, int[] yPoints) {
        int minX = Math.min(Math.min(xPoints[0], xPoints[1]), Math.min(xPoints[2], xPoints[3]));
        int maxX = Math.max(Math.max(xPoints[0], xPoints[1]), Math.max(xPoints[2], xPoints[3]));
        int minY = Math.min(Math.min(yPoints[0], yPoints[1]), Math.min(yPoints[2], yPoints[3]));
        int maxY = Math.max(Math.max(yPoints[0], yPoints[1]), Math.max(yPoints[2], yPoints[3]));

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (isPointInPolygon(x, y, xPoints, yPoints)) {
                    pixmap.drawPixel(x, y);
                }
            }
        }
    }

    private boolean isPointInPolygon(int x, int y, int[] xPoints, int[] yPoints) {
        boolean result = false;
        for (int i = 0, j = xPoints.length - 1; i < xPoints.length; j = i++) {
            if ((yPoints[i] > y) != (yPoints[j] > y) &&
                (x < (xPoints[j] - xPoints[i]) * (y - yPoints[i]) / (yPoints[j] - yPoints[i]) + xPoints[i])) {
                result = !result;
            }
        }
        return result;
    }
}
