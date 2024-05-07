package org.ken22.terrains;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.ken22.utils.MathUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * <p>This Class provides a simple API to generate height maps from a given Expression, and save them to a file.</p>
 * <p>The expected use case is that a height map is generated at run-time based on user input.
 * The resulting <code>heightmap.png</code> is then used for rendering the terrain.</p>
 * This allows for dynamic terrain generation.
 */
public class HeightMapGenerator {
    public static void main(String[] args) {
        String heightFunction = "0.4*(0.9 - e^(-(x^2 + y^2) / 8))";

        Expression e = new ExpressionBuilder(heightFunction)
            .variables("x", "y")
            .build()
            .setVariable("x", -1)
            .setVariable("y", -1);

        System.out.println(e.evaluate());

        generateHeightMap(e, "expheightmap");
    }
    private static final int SIZE = 64;
    private static final double RANGE = 5.0;
    private static final String OUTPUT_PATH = "assets/heightmaps/";


    /**
     * Generates and saves a heightmap <code>.png</code> in the <code>assets/heightmaps</code> directory.
     * A name must be specified for the generated file.
     * The default size is 64x64, but other size values may be set.
     *
     * @param heightFunction parsed Expression that describes the height of the terrain
     * @param mapName        name of the generated .png
     */
    public static void generateHeightMap(Expression heightFunction, String mapName) {
        double[][] heightMap = generate(heightFunction);
        save(heightMap, mapName);
    }

    /**
     * Generates and saves a heightmap <code>.png</code> in the <code>assets/heightmaps</code> directory.
     * A name must be specified for the generated file.
     * The default size is 64x64, but other size values may be set.
     *
     * @param heightFunction parsed Expression that describes the height of the terrain
     * @param mapName        name of the generated .png
     */
    public static void generateHeightMap(Expression heightFunction, int size, String mapName) {
        double[][] heightMap = generate(heightFunction, size);
        save(heightMap, mapName);
    }


    /**
     * Helper method that generates the heightmap 2d array.
     *
     * @param heightFunction parsed Expression that describes the height of the terrain
     * @return 2d array of height values
     */
    @SuppressWarnings("Duplicates")
    private static double[][] generate(Expression heightFunction) {
        double[][] heightMap = new double[SIZE][SIZE];
        double[] xCoords = MathUtils.linspace(-RANGE, RANGE, SIZE);
        double[] yCoords = MathUtils.linspace(-RANGE, RANGE, SIZE);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
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
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                heightMap[i][j] = (heightMap[i][j] - min) / (max - min);
            }
        }

        return heightMap;
    }

    /**
     * Helper method that generates the heightmap 2d array.
     *
     * @param heightFunction parsed Expression that describes the height of the terrain
     * @return 2d array of height values
     */
    @SuppressWarnings("Duplicates")
    private static double[][] generate(Expression heightFunction, int size) {
        double[][] heightMap = new double[size][size];
        double[] xCoords = MathUtils.linspace(-RANGE, RANGE, size);
        double[] yCoords = MathUtils.linspace(-RANGE, RANGE, size);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
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
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                heightMap[i][j] = (heightMap[i][j] - min) / (max - min);
            }
        }

        return heightMap;
    }


    /**
     * Helper method that saves the 2d array to an image.
     *
     * @param heightMap 2d array of height values
     * @param mapName   name of the file
     */
    private static void save(double[][] heightMap, String mapName) {
        // Remove trailing .png from filename if it was added by the client
        if (mapName.endsWith(".png")) {
            mapName = mapName.substring(0, mapName.length() - 4);
        }

        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int gray = (int) (heightMap[i][j] * 255);
                int rgb = (gray << 16) | (gray << 8) | gray;
                image.setRGB(i, j, rgb);
            }
        }

        try {
            ImageIO.write(image, "png", new File(OUTPUT_PATH + mapName + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
