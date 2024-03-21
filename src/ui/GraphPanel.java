package ui;

import javax.swing.*;

import interfaces.ODESolution;
import interfaces.ODESystem;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GraphPanel extends JPanel {
    private int width = 800;
    private int height = 600;
    
    private static final int LATTICE_DIST = 20; //in pixels
    private static final int PADDING = 50; //in pixels

    private static final int AXIS_WIDTH = 2;
    private static final int DOT_SIZE = 5;

    private final ODESystem odeSystem;
    private final ODESolution odeSolution;
    private final int[] entryIndexes;
    private final ArrayList<String> variableNames;

    private final UnitConvertor convertor;

    /**
     * A class to convert the unit norm to pixel norm.
     */
    class UnitConvertor {
        double xMin;
        double xMax;
        double yMin;
        double yMax;

        public UnitConvertor(double xMin, double xMax, double yMin, double yMax) {
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
        }

        public int xUnitToPixel(double x) {
            return (int) ((x - xMin) / (xMax - xMin) * (width - 2*PADDING) + PADDING);
        }

        public int yUnitToPixel(double y) {
            // screen coordinates start from bottom left corner -> y axis is inverted
            return (int) (((yMax - y) / (yMax - yMin) * (height - 2*PADDING)) + PADDING);
        }
    }

    private final Point origin;
    private final String xLabel;
    private final String yLabel;

    /**
     * expects the origin to be within the passed in limits.
     */
    public GraphPanel(ODESystem odeSystem, ODESolution odeSolution, String xVarName, String yVarName, int[] entryIndexes, int xMin, int xMax, int yMin, int yMax) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);

        this.odeSystem = odeSystem;
        this.odeSolution = odeSolution;
        this.entryIndexes = entryIndexes;
        this.variableNames = odeSystem.getVariables();

        this.convertor = new UnitConvertor(xMin, xMax, yMin, yMax);
        this.origin = new Point(convertor.xUnitToPixel(0), convertor.yUnitToPixel(0));

        this.xLabel = xVarName;
        this.yLabel = yVarName;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        this.width = getWidth();
        this.height = getHeight();
        Graphics2D g2d = (Graphics2D) g;

        // draw axis
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(AXIS_WIDTH));
        g2d.drawLine(0, origin.y, width, origin.y);
        g2d.drawString(xLabel, width - PADDING, origin.y + 15); 
        g2d.drawLine(origin.x, height-PADDING, origin.x, PADDING);
        g2d.drawString(yLabel, origin.x - 15, 30); 

        // draw origin
        g2d.fillOval(origin.x, origin.y, DOT_SIZE, DOT_SIZE);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        FontMetrics fm = g2d.getFontMetrics();

        // draw derivatives
        for(int h = PADDING; h < )

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

    private ArrayList<Double> derive(ArrayList<Double> vec) {
        // the method from the odesolver(for now just returning the vec)
        
        return vec;
    }
}
