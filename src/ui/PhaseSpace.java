package ui;

import javax.swing.*;
import java.awt.*;

public class PhaseSpace extends JFrame {

    private JTextField variableVField;
    private JTextField variableXField;
    private JButton generateButton;
    private GraphPanel graphPanel;

    public PhaseSpace() {
        setTitle("PhaseSpace");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel inputPanel = new JPanel(new GridLayout(3, 1));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel variableVLabel = new JLabel("Variable 1:");
        variableVField = new JTextField(10);
        inputPanel.add(variableVLabel);
        inputPanel.add(variableVField);

        JLabel variableXLabel = new JLabel("Variable 2:");
        variableXField = new JTextField(10);
        inputPanel.add(variableXLabel);
        inputPanel.add(variableXField);

        generateButton = new JButton("Generate");
        generateButton.addActionListener(e -> generateGraph());
        inputPanel.add(generateButton);

        add(inputPanel, BorderLayout.WEST);

        graphPanel = new GraphPanel();
        add(graphPanel, BorderLayout.CENTER);
    }

    private void generateGraph() {
        String variableV = variableVField.getText();
        String variableX = variableXField.getText();

        graphPanel.setHorizontalAxisLabel(variableV);
        graphPanel.setVerticalAxisLabel(variableX);

        // generating the graph?(i guess)but for now we re just making it visible
        graphPanel.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PhaseSpace gui = new PhaseSpace();
            gui.setVisible(true);
        });
    }
}
