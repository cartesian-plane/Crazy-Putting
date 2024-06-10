// SetupMenu.java
package org.ken22.ui;

//////////////////// IMPORTS //////////////////////////////////////////////
import javax.swing.*;
import java.awt.*;
//////////////////// IMPORTS //////////////////////////////////////////////



//////////////////// CLASS //////////////////////////////////////////////
public class SetupMenu {


    //////////////////// INITIALIZATIONS //////////////////////////////////////////////
    private JFrame frame;
    private JPanel opponentPanel;
    private JPanel levelInfoPanel;
    private Level selectedLevel;
    //////////////////// INITIALIZATIONS //////////////////////////////////////////////






    //Constructor that will take selected level
    public SetupMenu(Level selectedLevel) {
        this.selectedLevel = selectedLevel;
        frame = new JFrame("Setup Game");
        frame.setLayout(new BorderLayout(10, 10));

        opponentPanel = createOpponentPanel();
        levelInfoPanel = createLevelInfoPanel(selectedLevel);

        frame.add(opponentPanel, BorderLayout.CENTER);
        frame.add(levelInfoPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400); // Make the window larger
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }









    //////////////////// METHODS //////////////////////////////////////////////
    //buttons for opponents
    private JPanel createOpponentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Select Opponent"));

        JButton humanButton = createStyledButton("HUMAN");
        JButton aiButton = createStyledButton("AI");
        JButton ruleBasedAiButton = createStyledButton("RULE BASED AI");

        panel.add(humanButton);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(aiButton);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(ruleBasedAiButton);

        return panel;
    }


    //Show level info
    private JPanel createLevelInfoPanel(Level level) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Level Info"));

        panel.add(new JLabel("Name:"));
        panel.add(new JLabel(level.getName()));

        panel.add(new JLabel("Height Profile:"));
        panel.add(new JLabel(level.getHeightProfile()));

        panel.add(new JLabel("Friction Coefficient:"));
        panel.add(new JLabel(String.valueOf(level.getFrictionCoefficient())));

        panel.add(new JLabel("Start Position X:"));
        panel.add(new JLabel(String.valueOf(level.getStartPositionX())));

        panel.add(new JLabel("Start Position Y:"));
        panel.add(new JLabel(String.valueOf(level.getStartPositionY())));

        panel.add(new JLabel("Target Location X:"));
        panel.add(new JLabel(String.valueOf(level.getTargetLocationX())));

        panel.add(new JLabel("Target Location Y:"));
        panel.add(new JLabel(String.valueOf(level.getTargetLocationY())));

        panel.add(new JLabel("Target Radius:"));
        panel.add(new JLabel(String.valueOf(level.getTargetRadius())));

        return panel;
    }

    //button stuff
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 50));
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        return button;
    }
    //////////////////// METHODS //////////////////////////////////////////////




}
//////////////////// CLASS //////////////////////////////////////////////
