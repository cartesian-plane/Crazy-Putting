package ui;

import javax.swing.*;

import interfaces.ODESolution;
import interfaces.ODESystem;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PhaseSpace extends JFrame {

    private JComboBox<String> variable1ComboBox;
    private JComboBox<String> variable2ComboBox;
    private JButton generateButton;
    private GraphPanel graphPanel;

    private final ODESystem odeSystem;
    private final ODESolution odeSolution;

    public PhaseSpace(ODESystem odeSystem, ODESolution odeSolution) {
        this.odeSystem = odeSystem;
        this.odeSolution = odeSolution;

        setTitle("PhaseSpace");
        setSize(1200, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel inputPanel = new JPanel(new GridLayout(3, 1));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        
        List<String> variables = getVariablesFromODESystem();

        variable1ComboBox = new JComboBox<>(variables.toArray(new String[0]));
        variable2ComboBox = new JComboBox<>(variables.toArray(new String[0]));

        generateButton = new JButton("Generate");

        generateButton.addActionListener(e -> generateGraph());

        inputPanel.add(new JLabel("Variable 1:"));
        inputPanel.add(variable1ComboBox);
        inputPanel.add(new JLabel("Variable 2:"));
        inputPanel.add(variable2ComboBox);
        inputPanel.add(generateButton);

        add(inputPanel, BorderLayout.WEST);

        graphPanel = new GraphPanel(odeSystem, odeSolution, "A", "B", new int[]{0, 1}, 0, 10, 0, 10);
        add(graphPanel, BorderLayout.CENTER);

        graphPanel.setVisible(false); 
    }

    private void generateGraph() {
        String selectedVariable1 = (String) variable1ComboBox.getSelectedItem();
        String selectedVariable2 = (String) variable2ComboBox.getSelectedItem();

        graphPanel.repaint();
        graphPanel.setVisible(true);
    }

    private List<String> getVariablesFromODESystem() {

        return List.of("Variable A", "Variable B", "Variable C");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //PhaseSpace gui = new PhaseSpace();
            //gui.setVisible(true);
        });
    }
}
