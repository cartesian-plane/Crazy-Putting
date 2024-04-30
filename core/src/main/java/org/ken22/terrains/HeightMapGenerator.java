package org.ken22.terrains;

import org.ken22.utils.MathUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class HeightMapGenerator {
    private static final int SIZE = 64;
    private static final double RANGE = 5.0;
    private static final String OUTPUT_PATH = "assets/heightmaps/pillowmap.png";

    public static void main(String[] args) {
        double[][] heightMap = generate();
        save(heightMap);

        // this whole process would have been ~30 lines in Python, because we could have used numpy
        // for the boring Math utils stuff but Java is ❤️
    }

    public static double[][] generate() {
        double[][] heightMap = new double[SIZE][SIZE];
        double[] xCoords = MathUtils.linspace(-RANGE, RANGE, SIZE);
        double[] yCoords = MathUtils.linspace(-RANGE, RANGE, SIZE);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                heightMap[i][j] = h(xCoords[i], yCoords[j]);
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

    private static double h(double x, double y) {
        return (Math.sin((x - y) / 7) + 0.5);
    }

    private static void save(double[][] heightMap) {
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int gray = (int) (heightMap[i][j] * 255);
                int rgb = (gray << 16) | (gray << 8) | gray;
                image.setRGB(i, j, rgb);
            }
        }

        try {
            ImageIO.write(image, "png", new File(OUTPUT_PATH));
        } catch (Exception e) {
            System.out.println("OOps");
            e.printStackTrace();
        }
    }
}
