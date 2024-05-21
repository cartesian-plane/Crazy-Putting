//////////////////// IMPORTS //////////////////////////////////////////////
package org.ken22.players;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.HashMap;


import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.vectors.GVec4;

import java.util.ArrayList;

//////////////////// IMPORTS //////////////////////////////////////////////






//////////////////// CLASS //////////////////////////////////////////////
public class HumanPlayer implements Player {



//////////////////// INITIALIZATIONS //////////////////////////////////////////////
    private JFrame frame;
    private JPanel mainPanel;


    private JTextField VelYField, VelXField; // fields for inputting step size and time
    private JButton shootButton;              // button to generate results based on input

    private final ApplicationController app; // reference to the application controller

//////////////////// INITIALIZATIONS //////////////////////////////////////////////










 ////////////////////  GUI CONSTRUCTOR //////////////////////////////////////////////
    public HumanPlayer(ApplicationController app) {
        Font largerFont = new Font("SansSerif", Font.PLAIN, 18); //font



//////////////////// creating the structure //////////////////////////////////////////////

        frame = new JFrame("Shoot your shot");  //window name
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));


        //////////////////// creating the structure //////////////////////////////////////////////








////////////////////  Creating fields, buttons and naming them //////////////////////////////////////////////
        JPanel ShotCount = new JPanel();
        ShotCount.setLayout(new BoxLayout(ShotCount, BoxLayout.Y_AXIS));
        JLabel ShotCountLabel = new JLabel("Your shot count is" + shot);
        ShotCountLabel.setFont(largerFont);
        ShotCountLabel.add(ShotCountLabel);

        JPanel XLoc = new JPanel();
        XLoc.setLayout(new BoxLayout(XLoc, BoxLayout.Y_AXIS));
        JLabel XLocLabel = new JLabel("X position" + //add location);
        XLocLabel.setFont(largerFont);
        XLocLabel.add(XLocLabel);


        JPanel YLoc = new JPanel();
        YLoc.setLayout(new BoxLayout(YLoc, BoxLayout.Y_AXIS));
        JLabel YLocLabel = new JLabel("X position" + //add location);
        YLocLabel.setFont(largerFont);
        YLocLabel.add(YLocLabel);


        VelYField = createNumberField(largerFont);
        JPanel VelYPanel = new JPanel();
        VelYPanel.setLayout(new BoxLayout(VelYPanel, BoxLayout.Y_AXIS));
        JLabel VelYLabel = new JLabel("Velocity in Y");
        VelYLabel.setFont(largerFont);
        VelYPanel.add(VelYLabel);
        VelYPanel.add(VelYField);



        VelXField = createNumberField(largerFont);
        JPanel VelXPanel = new JPanel();
        VelXPanel.setLayout(new BoxLayout(VelXPanel, BoxLayout.Y_AXIS));
        JLabel VelXLabel = new JLabel("Velocity in X");
        VelXLabel.setFont(largerFont);
        VelXPanel.add(VelXLabel);
        VelXPanel.add(VelXField);





        //button stuff
        generateButton = new JButton("SHOOT");
        generateButton.setFont(largerFont);
        generateButton.addActionListener(e -> {
            try {
           
                double VelY = !VelYField.getText().isEmpty() ? Double.parseDouble(VelYField.getText()) : 0.0;
                double VelX = !VelXField.getText().isEmpty() ? Double.parseDouble(VelXField.getText()) : 0.0;

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numbers", "Input error", JOptionPane.ERROR_MESSAGE);
            }
        });






////////////////////  Creating fields, buttons and naming them //////////////////////////////////////////////





////////////////////  COntrol panel //////////////////////////////////////////////
        JPanel controlsPanel = new JPanel();
        controlsPanel.add(ShotCountPanel);
        controlsPanel.add(XLoc);
        controlsPanel.add(YLoc);
        controlsPanel.add(VelYPanel);
        controlsPanel.add(VelYField);
        controlsPanel.add(VelXPanel);
        controlsPanel.add(VelxField);
        controlsPanel.add(generateButton);



        mainPanel.add(controlsPanel);
        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
 ////////////////////  COntrol panel //////////////////////////////////////////////

    }

 ////////////////////  GUI CONSTRUCTOR //////////////////////////////////////////////









 ////////////////////  METHODS //////////////////////////////////////////////



 @Override
    public ArrayList<Double> play(StateVector4 state, GolfCourse course, int shot) {
        return null;
    }




    // Method that creates a text field for numeric input only
    private JTextField createNumberField(Font font) {
        JTextField numberField = new JTextField("X", 7);
        numberField.setFont(font);
        numberField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) {
                    return;
                }
                if (str.matches("[0-9]*\\.?[0-9]*")) { // if its a number
                    super.insertString(offs, str, a);
                }
            }
        });
        return numberField;
    }

 ////////////////////  METHODS //////////////////////////////////////////////












//////////////////// MAIn //////////////////////////////////////////////
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            ApplicationController app = new ApplicationController();
            InputPage InputPage = new InputPage(app);
        });
    }
//////////////////// MAIN //////////////////////////////////////////////

}


//////////////////// CLASS //////////////////////////////////////////////
