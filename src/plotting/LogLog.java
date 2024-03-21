//package org.example;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.axis.LogarithmicAxis;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;
//
//import javax.swing.*;
//import java.awt.*;
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//
//public class LogLog {
//    public static void main(String[] args) {
//        String csvFile = "/Users/vishalvenkat/Desktop/java/Phase-solver/euler.csv";
//        String line;
//        String csvSplitBy = ",";
//        XYSeries series = new XYSeries("Data");
//
//        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
//            while ((line = br.readLine()) != null) {
//                String[] values = line.split(csvSplitBy);
//                double x = Double.parseDouble(values[0].trim());
//                double y = Double.parseDouble(values[1].trim());
//                series.add(x, y);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        XYSeriesCollection dataset = new XYSeriesCollection();
//        dataset.addSeries(series);
//
//        JFreeChart chart = ChartFactory.createXYLineChart(
//                "Log-Log Plot",
//                "X",
//                "Y",
//                dataset
//        );
//        chart.getXYPlot().setDomainAxis(new LogarithmicAxis("X"));
//        chart.getXYPlot().setRangeAxis(new LogarithmicAxis("Y"));
//
//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new Dimension(800, 600));
//        JFrame frame = new JFrame("Log-Log Plot Example");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(chartPanel);
//        frame.pack();
//        frame.setVisible(true);
//    }
//}