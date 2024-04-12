package org.ken22.interfaces;//package org.ken22.interfaces;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.util.ArrayList;
//
//public class Table {
//
//    private JFrame frame;
//    private JTable table;
//    private JScrollPane scrollPane;
//
//    public Table(ArrayList<Double> time, ArrayList<String> variables, ArrayList<ArrayList<Double>> stateVectors) {
//        initializeTable(time, variables, stateVectors);
//    }
//
//    private void initializeTable(ArrayList<Double> time, ArrayList<String> variables, ArrayList<ArrayList<Double>> stateVectors) {
//        // Initialize column names array with time followed by variables
//        String[] columnNames = new String[variables.size() + 1];
//        columnNames[0] = "Time";
//        for (int i = 0; i < variables.size(); i++) {
//            columnNames[i + 1] = variables.get(i);
//        }
//
//        // Convert the ArrayList<ArrayList<Double>> stateVectors to Object[][]
//        Object[][] data = new Object[stateVectors.size()][variables.size() + 1];
//        for (int i = 0; i < stateVectors.size(); i++) {
//            data[i][0] = time.get(i); // Time
//            for (int j = 0; j < variables.size(); j++) {
//                data[i][j + 1] = stateVectors.get(i).get(j); // State vector for each variable
//            }
//        }
//
//        // Create a table with data and column names
//        table = new JTable(new DefaultTableModel(data, columnNames));
//
//        // Create a scroll pane and add the table to it
//        scrollPane = new JScrollPane(table);
//
//        // Create a frame and add the scroll pane to it
//        frame = new JFrame("Table with State Vectors");
//        frame.add(scrollPane);
//
//        // Set frame properties
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(800, 600);
//        frame.setLocationRelativeTo(null); // Center the frame
//        frame.setVisible(true); // Set frame visible after adding all components
//    }
//
//    public static void main(String[] args) {
//        // Generate the data from ODEsolution
//        ODESolution solution = new ODESolution(time,stateVectors);
//        ODESystem system = new ODESystem(varOrder,initialStateVector, functions);
//
//        // Use the ArrayLists directly to create the table
//        SwingUtilities.invokeLater(() -> new Table(solution.time, system.getVariables(), solution.getStateVectors()));
//    }
//}
