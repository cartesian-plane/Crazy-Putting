package org.ken22.ui;

import org.ken22.interfaces.ODESolution;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class Table extends JFrame {

    private ODESolution solution;

    private JFrame frame;
    private JTable table;
    private JScrollPane scrollPane;

    public Table(ArrayList<String> variables, ODESolution solution) {

        this.solution = solution;
        ArrayList<ArrayList<Double>> stateVectors = solution.getStateVectors();
        ArrayList<Double> time = solution.getTime();

        // Initialize column names array with time followed by variables
        String[] columnNames = new String[variables.size() + 1];
        columnNames[0] = "Time";
        for (int i = 0; i < variables.size(); i++) {
            columnNames[i + 1] = variables.get(i);
        }

        // Convert the ArrayList<ArrayList<Double>> stateVectors to Object[][]
        Object[][] data = new Object[stateVectors.size()][variables.size() + 1];
        for (int i = 0; i < stateVectors.size(); i++) {
            data[i][0] = time.get(i); // Time
            for (int j = 0; j < variables.size(); j++) {
                data[i][j + 1] = stateVectors.get(i).get(j); // State vector for each variable
            }
        }

        // Create a table with data and column names
        table = new JTable(new DefaultTableModel(data, columnNames));

        // Create a scroll pane and add the table to it
        scrollPane = new JScrollPane(table);

        // Create a frame and add the scroll pane to it
        this.setTitle("Table with State Vectors");
        this.add(scrollPane);

        // Set frame properties
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null); // Center the frame
        this.setVisible(true); // Set frame visible after adding all components
    }
}
