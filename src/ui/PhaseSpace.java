package ui;

import javax.swing.*;
import java.awt.*;

public class PhaseSpace extends JFrame {

    private JComboBox<String> variable1ComboBox;
    private JComboBox<String> variable2ComboBox;
    private JButton generateButton;
    private GraphPanel graphPanel;

    public PhaseSpace() {
        setTitle("PhaseSpace");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel inputPanel = new JPanel(new GridLayout(3, 1));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        variable1ComboBox = new JComboBox<>(new String[]{"Variable A", "Variable B", "Variable C"});
        variable2ComboBox = new JComboBox<>(new String[]{"Variable X", "Variable Y" , "Variabel Z"});


        add(new JLabel("Variable 1:"));
        add(variable1ComboBox);
        add(new JLabel(" Vazriable 2:"));
        add(variable2ComboBox);

        generateButton = new JButton("Generate");
        generateButton.addActionListener(e -> generateGraph());
        inputPanel.add(generateButton);

        add(inputPanel, BorderLayout.WEST);

        //graphPanel = new GraphPanel();
        //add(graphPanel, BorderLayout.CENTER);
    }

    private void generateGraph() {
        String selectedVariable1 = (String) variable1ComboBox.getSelectedItem();
        String selectedVariable2 = (String) variable2ComboBox.getSelectedItem();


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
