package ui;

import javax.swing.*;

import interfaces.ODESolution;
import interfaces.ODESystem;

import java.awt.*;
import java.util.ArrayList;

public class GraphPanel extends JPanel {
    private int width = 800;
    private int height = 600;
    
    private static final int LATTICE_DIST = 20; //in pixels
    private static final int PADDING = 50; //in pixels

    private static final int AXIS_WIDTH = 2;
    private static final int DOT_SIZE = 5;

    private final int xMin, yMin;
    private final int xMax, yMax;

    private final ODESystem odeSystem;
    private final ODESolution odeSolution;
    private final int[] entryIndexes; // 2d array of the chose variable indexes
    private final ArrayList<String> variableNames; // ordered list of the two variable names

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
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
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
        for(double h = PADDING; h < height - PADDING; h += LATTICE_DIST) {
            for(double w = PADDING; w < width - PADDING; w += LATTICE_DIST) {

                ArrayList<Double> vec = currentVector(w, h);
                
                double xVec = convertor.xUnitToPixel(vec.get(entryIndexes[0]));
                double yVec = convertor.yUnitToPixel(vec.get(entryIndexes[1]));
                
                ArrayList<Double> der = odeSystem.derivative(vec);

                double xDer = convertor.xUnitToPixel(der.get(entryIndexes[0]));
                double yDer = convertor.yUnitToPixel(der.get(entryIndexes[1]));

                drawArrow(g2d, (int)xVec, (int)yVec, (int)(xVec + xDer), (int)(yVec - yDer));
            }
        }

        // x axis tickmarcks
        for (int i = xMin; i <= xMax; i++) {
            int xMark = origin.x + (i * (width - origin.x) / (xMax-xMin));
            g2d.drawLine(xMark, origin.y - 3, xMark, origin.y + 3);

            String label = Integer.toString(i);
            int labelWidth = fm.stringWidth(label);
            g2d.drawString(label, xMark - labelWidth / 2, origin.y + 20); 

            g2d.drawString(label, origin.x - labelWidth - 10, xMark + fm.getAscent() / 2);

            if (i == xMax) {
                g2d.drawString(xLabel, xMark - labelWidth / 2, origin.y + 20);
            }
        }

        // y axis tickmarcks
        for (int i = yMin; i <= yMax; i++) {
            int yMark = origin.y - (i * origin.y / (xMax-xMin));

            g2d.drawLine(origin.x - 3, yMark, origin.x + 3, yMark);

            String label = Integer.toString(i);
            int labelWidth = fm.stringWidth(label);
            g2d.drawString(label, yMark - labelWidth / 2, origin.y + 20); 

            g2d.drawString(label, origin.x - labelWidth - 10, yMark + fm.getAscent() / 2);

            if (i == yMax) {
                g2d.drawString(yLabel, origin.x - labelWidth - 10, yMark + fm.getAscent() / 2);
            }
        }

        if (odeSolution != null) {
            g2d.setColor(Color.RED);

            // initial point
            ArrayList<Double> vec = odeSolution.getStateVectors().get(0);
            int xPrev = convertor.xUnitToPixel(vec.get(entryIndexes[0]));
            int yPrev = convertor.yUnitToPixel(vec.get(entryIndexes[1]));

            g2d.fillOval((int)xPrev - 2, (int)yPrev - 2, 4, 4);

            for (ArrayList<Double> nextVec : odeSolution.getStateVectors()) {
                int xNext = convertor.xUnitToPixel(nextVec.get(entryIndexes[0]));
                int yNext = convertor.yUnitToPixel(nextVec.get(entryIndexes[1]));
                
                g2d.fillOval(xNext - 2, yNext - 2, 4, 4);

                if (xPrev != 0 && yPrev != 0) {
                    drawArrow(g2d, xPrev, yPrev, xNext, yNext);
                }

                xPrev = xNext;
                yPrev = yNext;
            }
        }
    }

    private ArrayList<Double> currentVector(double w, double h) {
        ArrayList<Double> vec = (ArrayList<Double>)odeSystem.getInitialStateVector().clone();
        vec.set(entryIndexes[0], w);
        vec.set(entryIndexes[1], h);
        return vec;
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
