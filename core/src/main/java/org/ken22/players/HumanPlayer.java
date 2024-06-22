package org.ken22.players;

//////////////////// IMPORTS //////////////////////////////////////////////
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.vectors.StateVector4;
//////////////////// IMPORTS //////////////////////////////////////////////

//////////////////// CLASS //////////////////////////////////////////////
//for prompting the player to shoot
public class HumanPlayer implements Player {

    //////////////////// INITIALIZATIONS //////////////////////////////////////////////
    private JFrame frame;
    private JPanel mainPanel;

    private JTextField VelYField, VelXField;
    private JButton shootButton;
    private JLabel shotCountLabel;
    private JLabel xLocLabel;
    private JLabel yLocLabel;


    private int shotCount;
    private double xPosition;
    private double yPosition;


    private double velocityX;
    private double velocityY;
    private boolean shotTaken;
    //////////////////// INITIALIZATIONS //////////////////////////////////////////////

    ////////////////////  GUI CONSTRUCTOR //////////////////////////////////////////////

    public HumanPlayer() {
        this.shotCount = 0;
        this.xPosition = 0;
        this.yPosition = 0;
        this.shotTaken = false;

        //hopefully pretty
        Font largerFont = new Font("SansSerif", Font.BOLD, 18);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 20);

        frame = new JFrame("Shoot your shot");
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(60, 63, 65));













        // Shot count display
        JPanel shotCountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        shotCountPanel.setBackground(new Color(60, 63, 65));
        shotCountLabel = new JLabel("Your shot count is: " + shotCount);
        shotCountLabel.setFont(largerFont);
        shotCountLabel.setForeground(Color.WHITE);
        shotCountPanel.add(shotCountLabel);




        // X Location display
        JPanel xLocPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        xLocPanel.setBackground(new Color(60, 63, 65));
        xLocLabel = new JLabel("X position: " + xPosition);
        xLocLabel.setFont(largerFont);
        xLocLabel.setForeground(Color.WHITE);
        xLocPanel.add(xLocLabel);

        // Y Location display
        JPanel yLocPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        yLocPanel.setBackground(new Color(60, 63, 65));
        yLocLabel = new JLabel("Y position: " + yPosition);
        yLocLabel.setFont(largerFont);
        yLocLabel.setForeground(Color.WHITE);
        yLocPanel.add(yLocLabel);




        // Velocity Y field
        VelYField = createNumberField(largerFont);
        JPanel VelYPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        VelYPanel.setBackground(new Color(60, 63, 65));
        JLabel VelYLabel = new JLabel("Velocity in Y");
        VelYLabel.setFont(largerFont);
        VelYLabel.setForeground(Color.WHITE);
        VelYPanel.add(VelYLabel);
        VelYPanel.add(VelYField);

        // Velocity X field
        VelXField = createNumberField(largerFont);
        JPanel VelXPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        VelXPanel.setBackground(new Color(60, 63, 65));
        JLabel VelXLabel = new JLabel("Velocity in X");
        VelXLabel.setFont(largerFont);
        VelXLabel.setForeground(Color.WHITE);
        VelXPanel.add(VelXLabel);
        VelXPanel.add(VelXField);


        // Buttone
        shootButton = new JButton("SHOOT");
        shootButton.setFont(buttonFont);
        shootButton.setBackground(new Color(70, 130, 180));
        shootButton.setForeground(Color.WHITE);
        shootButton.setFocusPainted(false);
        shootButton.addActionListener(e -> handleShootButton());














        // Control panel spawn
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setBackground(new Color(60, 63, 65));
        controlsPanel.add(shotCountPanel);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlsPanel.add(xLocPanel);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlsPanel.add(yLocPanel);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlsPanel.add(VelYPanel);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlsPanel.add(VelXPanel);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        controlsPanel.add(shootButton);

        mainPanel.add(controlsPanel);
        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    ////////////////////  GUI CONSTRUCTOR //////////////////////////////////////////////

    ////////////////////  METHODS //////////////////////////////////////////////

    //THE method that will take the state and give player option to shoot using vel in y and x
    public StateVector4 play(StateVector4 state) {
        this.xPosition = state.x();
        this.yPosition = state.y();
        updateLabels();

        shotTaken = false;
        while (!shotTaken) {
            try {
                Thread.sleep(100); // Wait for user input
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return new StateVector4(xPosition, yPosition, velocityX, velocityY);
    }



    //button machine stuff checks as well
    private void handleShootButton() {
        try {
            velocityY = !VelYField.getText().isEmpty() ? Double.parseDouble(VelYField.getText()) : 0.0;
            velocityX = !VelXField.getText().isEmpty() ? Double.parseDouble(VelXField.getText()) : 0.0;
            shotTaken = true;
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numbers", "Input error", JOptionPane.ERROR_MESSAGE);
        }
    }



    // method that creates a text field for numbers from way before
    private JTextField createNumberField(Font font) {
        JTextField numberField = new JTextField("0", 7);
        numberField.setFont(font);
        numberField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) {
                    return;
                }
                if (str.matches("[0-9]*\\.?[0-9]*")) {
                    super.insertString(offs, str, a);
                }
            }
        });
        return numberField;
    }


    //updating the labels with current info
    private void updateLabels() {
        shotCountLabel.setText("Your shot count is: " + shotCount);
        xLocLabel.setText("X position: " + xPosition);
        yLocLabel.setText("Y position: " + yPosition);
    }
    ////////////////////  METHODS //////////////////////////////////////////////

    //////////////////// MAIN //////////////////////////////////////////////
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HumanPlayer();
        });
    }
    //////////////////// MAIN //////////////////////////////////////////////
}
//////////////////// CLASS //////////////////////////////////////////////
