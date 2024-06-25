import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * A JPanel for displaying a log-log plot of errors against step sizes.
 */
public class LogLogPlot extends JPanel {
    private final ArrayList<Double> stepSizes;
    private final ArrayList<Double> errorsEulerList;
    private final ArrayList<Double> errorsRK2List;
    private final ArrayList<Double> errorsRK4List;

    private final double slopeEuler;
    private final double slopeRK2;
    private final double slopeRK4;

    /**
     * Constructs a new LogLogPlot.
     * @param stepSizes The list of step sizes.
     * @param errorsEulerList The list of errors for the Euler method.The errorList is calculated by the "ErrorCalculator.java" class.
     * @param errorsRK2List The list of errors for the RK2 method.The errorList is calculated by the "ErrorCalculator.java" class.
     * @param errorsRK4List The list of errors for the RK4 method.The errorList is calculated by the "ErrorCalculator.java" class.
     */
    public LogLogPlot(ArrayList<Double> stepSizes, ArrayList<Double> errorsEulerList, ArrayList<Double> errorsRK2List, ArrayList<Double> errorsRK4List) {
        this.stepSizes = stepSizes;
        this.errorsEulerList = errorsEulerList;
        this.errorsRK2List = errorsRK2List;
        this.errorsRK4List = errorsRK4List;

        // Calculate slopes for each solver
        this.slopeEuler = findSlope(stepSizes, errorsEulerList);
        this.slopeRK2 = findSlope(stepSizes, errorsRK2List);
        this.slopeRK4 = findSlope(stepSizes, errorsRK4List);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();

        // Draw axes
        g2.drawLine(50, height - 50, width - 50, height - 50); // x-axis
        g2.drawLine(50, height - 50, 50, 50); // y-axis

        // Draw title
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Log-Log Plot", width / 2 - 50, 30);

        // Draw labels
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("Step Size", width / 2 - 30, height - 20);
        AffineTransform orig = g2.getTransform();
        g2.rotate(-Math.PI / 2);
        g2.drawString("Global Error", -height / 2 - 30, 20);
        g2.setTransform(orig);

        // Logarithmic scale conversion
        double minX = Math.log10(stepSizes.get(0));
        double maxX = Math.log10(stepSizes.get(stepSizes.size() - 1));
        double minY = Math.log10(Math.min(getMin(errorsEulerList), Math.min(getMin(errorsRK2List), getMin(errorsRK4List))));
        double maxY = Math.log10(Math.max(getMax(errorsEulerList), Math.max(getMax(errorsRK2List), getMax(errorsRK4List))));

        // Draw tick marks and labels
        drawLogTicks(g2, minX, maxX, height - 50, 50, width - 50, true);  // x-axis
        drawLogTicks(g2, minY, maxY, 50, height - 50, 50, false); // y-axis

        // Plot points and lines for the Euler error list
        g2.setColor(Color.RED);
        plotData(g2, stepSizes, errorsEulerList, minX, maxX, minY, maxY, width, height);

        // Plot points and lines for the RK2 error list
        g2.setColor(Color.BLUE);
        plotData(g2, stepSizes, errorsRK2List, minX, maxX, minY, maxY, width, height);

        // Plot points and lines for the RK4 error list
        g2.setColor(new Color(128, 0, 128)); // Purple color for RK4
        plotData(g2, stepSizes, errorsRK4List, minX, maxX, minY, maxY, width, height);

        // Set dashed stroke for grid lines
        float[] dashPattern = { 5, 5 };
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0);
        g2.setStroke(dashed);
        g2.setColor(Color.LIGHT_GRAY);

        // Draw legend
        drawLegend(g2, width, height);
    }

    /**
     * Plots the data points and lines connecting them.
     * @param g2 The Graphics2D object.
     * @param stepSizes The list of step sizes.
     * @param errors The list of errors.
     * @param minX The minimum x value in log scale.
     * @param maxX The maximum x value in log scale.
     * @param minY The minimum y value in log scale.
     * @param maxY The maximum y value in log scale.
     * @param width The width of the plot area.
     * @param height The height of the plot area.
     */
    private void plotData(Graphics2D g2, ArrayList<Double> stepSizes, ArrayList<Double> errors, double minX, double maxX, double minY, double maxY, int width, int height) {
        for (int i = 0; i < stepSizes.size(); i++) {
            if (i > 0) {
                double x1 = Math.log10(stepSizes.get(i - 1));
                double y1 = Math.log10(errors.get(i - 1));
                double x2 = Math.log10(stepSizes.get(i));
                double y2 = Math.log10(errors.get(i));

                int x1Screen = (int) (50 + (x1 - minX) / (maxX - minX) * (width - 100));
                int y1Screen = (int) (height - 50 - (y1 - minY) / (maxY - minY) * (height - 100));
                int x2Screen = (int) (50 + (x2 - minX) / (maxX - minX) * (width - 100));
                int y2Screen = (int) (height - 50 - (y2 - minY) / (maxY - minY) * (height - 100));

                g2.draw(new Line2D.Double(x1Screen, y1Screen, x2Screen, y2Screen));
            }
        }
    }

    /**
     * Draws the logarithmic ticks on the axes.
     * @param g2 The Graphics2D object.
     * @param minLog The minimum logarithmic value.
     * @param maxLog The maximum logarithmic value.
     * @param axisPos The axis position.
     * @param start The start position of the axis.
     * @param end The end position of the axis.
     * @param isXAxis Indicates if it is the x-axis.
     */
    private void drawLogTicks(Graphics2D g2, double minLog, double maxLog, int axisPos, int start, int end, boolean isXAxis) {
        int tickSize = 5; // Set tick size
        Font originalFont = g2.getFont();
        Font smallFont = new Font("Arial", Font.PLAIN, 10); // Smaller font size for tick labels
        g2.setFont(smallFont);

        for (int i = (int) Math.floor(minLog); i <= Math.ceil(maxLog); i++) {
            int pos = (int) (start + ((i - minLog) / (maxLog - minLog)) * (end - start));
            String label = "10^" + i;
            if (isXAxis) {
                g2.drawLine(pos, axisPos, pos, axisPos - tickSize);
                g2.drawString(label, pos - 10, axisPos + 15); // Adjusted position for x-axis labels
            } else {
                g2.drawLine(axisPos, pos, axisPos + tickSize, pos);
                AffineTransform orig = g2.getTransform();
                g2.rotate(-Math.PI / 2);
                g2.drawString(label, -pos - 10, axisPos - 5); // Adjusted position for y-axis labels
                g2.setTransform(orig);
            }
        }

        g2.setFont(originalFont); // Restore original font
    }

    /**
     * Draws the legend on the plot.
     * @param g2 The Graphics2D object.
     * @param width The width of the plot area.
     * @param height The height of the plot area.
     */
    private void drawLegend(Graphics2D g2, int width, int height) {
        int legendX = width - 150;
        int legendY = height - 150;

        g2.setFont(new Font("Arial", Font.PLAIN, 12));

        // Draw legend box
        g2.setColor(Color.WHITE);
        g2.fillRect(legendX, legendY, 150, 100);
        g2.setColor(Color.BLACK);
        g2.drawRect(legendX, legendY, 150, 100);

        // Draw legend entries
        g2.setColor(Color.RED);
        g2.drawString("Euler (slope: " + String.format("%.2f", slopeEuler) + ")", legendX + 20, legendY + 20);
        g2.drawLine(legendX + 5, legendY + 15, legendX + 15, legendY + 15);

        g2.setColor(Color.BLUE);
        g2.drawString("RK2 (slope: " + String.format("%.2f", slopeRK2) + ")", legendX + 20, legendY + 40);
        g2.drawLine(legendX + 5, legendY + 35, legendX + 15, legendY + 35);

        g2.setColor(new Color(128, 0, 128)); // Purple color for RK4
        g2.drawString("RK4 (slope: " + String.format("%.2f", slopeRK4) + ")", legendX + 20, legendY + 60);
        g2.drawLine(legendX + 5, legendY + 55, legendX + 15, legendY + 55);
    }

    /**
     * Gets the minimum value from a list of doubles.
     * @param list The list of doubles.
     * @return The minimum value.
     */
    private double getMin(ArrayList<Double> list) {
        return list.stream().min(Double::compare).get();
    }

    /**
     * Gets the maximum value from a list of doubles.
     * @param list The list of doubles.
     * @return The maximum value.
     */
    private double getMax(ArrayList<Double> list) {
        return list.stream().max(Double::compare).get();
    }

    /**
     * Finds the slope of the line in the log-log plot for the given data.
     * @param xData The x data points.
     * @param yData The y data points.
     * @return The slope of the line.
     */
    private double findSlope(ArrayList<Double> xData, ArrayList<Double> yData) {
        int n = xData.size();
        double sumX = 0.0, sumY = 0.0, sumXY = 0.0, sumXX = 0.0;

        for (int i = 0; i < n; i++) {
            double logX = Math.log10(xData.get(i));
            double logY = Math.log10(yData.get(i));
            sumX += logX;
            sumY += logY;
            sumXY += logX * logY;
            sumXX += logX * logX;
        }

        return (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
    }
}
