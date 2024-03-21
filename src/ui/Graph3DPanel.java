package ui;

import javax.swing.*;

import interfaces.ODESolution;
import interfaces.ODESystem;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Graph3DPanel extends JPanel {

    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    
    private static final int LATTICE_DIST = 20; //in pixels
    private static final int PADDING = 50; //in pixels

    private static final int AXIS_WIDTH = 2;
    private static final int DOT_SIZE = 6;

    private final int xMin, yMin, zMin;
    private final int xMax, yMax, zMax;

    private final ODESystem odeSystem;
    private final ODESolution odeSolution;

    private final int[] entryIndexes;

    class Point3D {
        double x;
        double y;
        double z;

        public Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    /**
     * A class to map the 3D coordinates in unit norm to 2D coordinates in pixel norm.
     */
    class Mapper {
        //screen coordinated in pixel norm
        double xMin;
        double xMax;
        double yMin;
        double yMax;

        static final double[] xBasis = {-1.0, 2.0, -1.0};
        static final double[] yBasis = {1.0, 0.0, -1.0};

        double[][] projectionMatrix = {
            {2, -1, -1},
            {-1, 2, -1},
            {-1, -1, 2}
        };

        Point project(ArrayList<Double> vec) {
            double[] projection = new double[3];
            for (int i = 0; i < 3; i++) {
                projection[i] = 0;
                for (int j = 0; j < 3; j++) {
                    projection[i] += projectionMatrix[i][j] * vec.get(entryIndexes[j]).doubleValue();
                }
            }
            return null;
        }


        public Mapper(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax) {
            
        }
    }

    private Point3D absOrigin;
    private Point origin;
    private String xAxisLabel;
    private String yAxisLabel;

    public Graph3DPanel(ODESystem odeSystem, ODESolution odeSolution, int[] entryIndexes, int xMin, int xMax, int yMin, int yMax, int zMin, int zMax) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);

        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.zMin = zMin;
        this.zMax = zMax;

        this.odeSystem = odeSystem;
        this.odeSolution = odeSolution;
        this.entryIndexes = entryIndexes;

        absOrigin = new Point3D(xMax, yMax, zMax);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(AXIS_WIDTH));
        g2d.drawLine(origin.x, origin.y, width, origin.y);
        g2d.drawString(horizontalAxisLabel, width - 60, origin.y + 10); 

        g2d.drawLine(origin.x, origin.y, origin.x, 0);
        g2d.drawString(verticalAxisLabel, origin.x + 10, 10); 

        int x = origin.x - DOT_SIZE / 2;
        int y = height - origin.y - DOT_SIZE / 2;
        g2d.fillOval(x, y, DOT_SIZE, DOT_SIZE);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        FontMetrics fm = g2d.getFontMetrics();
        for (int i = 1; i <= 10; i++) {
            int xMark = origin.x + (i * (width - origin.x) / 10);
            int yMark = origin.y - (i * origin.y / 10);
            g2d.drawLine(xMark, origin.y - 3, xMark, origin.y + 3);
            g2d.drawLine(origin.x - 3, yMark, origin.x + 3, yMark);

            String label = Integer.toString(i);
            int labelWidth = fm.stringWidth(label);
            g2d.drawString(label, xMark - labelWidth / 2, origin.y + 20); 

            if (i == 10 && xMark + labelWidth / 2 > width) {
                g2d.drawString(label, width - labelWidth - 5, origin.y + 20); 
            } else {
                g2d.drawString(label, xMark - labelWidth / 2, origin.y + 20); 
            }

            g2d.drawString(label, origin.x - labelWidth - 10, yMark + fm.getAscent() / 2);
        }

        g2d.setColor(Color.BLACK); 
        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                if (i % 5 == 0 && j % 5 == 0) { 
                    int xCoord = origin.x + (i * (width - origin.x) / 10);
                    int yCoord = origin.y - (j * origin.y / 10);
                    int xState = (i * (width - origin.x) / 10);
                    int yState = (j * origin.y / 10);

                    ArrayList<Double> vec = new ArrayList<>();
                    vec.add((double) xState);
                    vec.add((double) yState);
                    ArrayList<Double> derivative = derive(vec);

                    double scale = 0.2;
                    double dx = scale * derivative.get(0);
                    double dy = scale * derivative.get(1);

                    int startX = xCoord;
                    int startY = yCoord;
                    int endX = startX + (int) dx;
                    int endY = startY - (int) dy;
                    drawArrow(g2d, startX, startY, endX, endY);
                }
            }
        }

        if (odeSolution != null) {
            g2d.setColor(Color.RED);
            int prevX = 0, prevY = 0;
            for (ArrayList<Double> sublist : odeSolution) {
                double xCoord = sublist.get(variableIndexMap.get(horizontalAxisLabel));
                double yCoord = sublist.get(variableIndexMap.get(verticalAxisLabel));
                int xPlot = origin.x + (int) (xCoord * (width - origin.x) / 10);
                int yPlot = origin.y - (int) (yCoord * origin.y / 10);
                g2d.fillOval(xPlot - 2, yPlot - 2, 4, 4);

                if (prevX != 0 && prevY != 0) {
                    drawArrow(g2d, prevX, prevY, xPlot, yPlot);
                }

                prevX = xPlot;
                prevY = yPlot;
            }
        }
    }

    private void drawArrow(Graphics2D g2d, int startX, int startY, int endX, int endY) {
        g2d.drawLine(startX, startY, endX, endY);

        double angle = Math.atan2(endY - startY, endX - startX);
        int arrowSize = 8;

        int x1 = (int) (endX - arrowSize * Math.cos(angle + Math.PI / 6));
        int y1 = (int) (endY - arrowSize * Math.sin(angle + Math.PI / 6));
        int x2 = (int) (endX - arrowSize * Math.cos(angle - Math.PI / 6));
        int y2 = (int) (endY - arrowSize * Math.sin(angle - Math.PI / 6));

        g2d.drawLine(endX, endY, x1, y1);
        g2d.drawLine(endX, endY, x2, y2);
    }
}
