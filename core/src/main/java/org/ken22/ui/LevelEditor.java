package org.ken22.ui;

//////////////////// IMPORTS //////////////////////////////////////////////
import org.ken22.interfaces.Level;
import javax.swing.*;
import java.awt.*;
//////////////////// IMPORTS //////////////////////////////////////////////



//////////////////// CLASS //////////////////////////////////////////////
public class LevelEditor {



    //////////////////// INITIALIZATIONS //////////////////////////////////////////////
    private JFrame frame;
    private Level level;
    private JTextField nameField;
    private JTextField heightProfileField;
    private JTextField frictionCoefficientField;
    private JTextField startPositionXField;
    private JTextField startPositionYField;
    private JTextField targetLocationXField;
    private JTextField targetLocationYField;
    private JTextField targetRadiusField;
    //////////////////// INITIALIZATIONS //////////////////////////////////////////////




    //listen for changes
    private LevelEditListener editListener;

    public interface LevelEditListener {
        void onLevelEdited(Level level);
    }





    //constucro
    public LevelEditor(Level level, LevelEditListener listener) {
        this.editListener = listener;
        this.level = level;
        frame = new JFrame("Edit Level: " + level.getName());
        frame.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 2, 10, 10)); 
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        



        //fields for changin
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField(level.getName());
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Height profile:"));
        heightProfileField = new JTextField(level.getHeightProfile());
        formPanel.add(heightProfileField);

        formPanel.add(new JLabel("Friction Coefficient:"));
        frictionCoefficientField = new JTextField(String.valueOf(level.getFrictionCoefficient()));
        formPanel.add(frictionCoefficientField);

        formPanel.add(new JLabel("Start Position X:"));
        startPositionXField = new JTextField(String.valueOf(level.getStartPositionX()));
        formPanel.add(startPositionXField);
    
        formPanel.add(new JLabel("Start Position Y:"));
        startPositionYField = new JTextField(String.valueOf(level.getStartPositionY()));
        formPanel.add(startPositionYField);
    
        formPanel.add(new JLabel("Target Location X:"));
        targetLocationXField = new JTextField(String.valueOf(level.getTargetLocationX()));
        formPanel.add(targetLocationXField);
    
        formPanel.add(new JLabel("Target Location Y:"));
        targetLocationYField = new JTextField(String.valueOf(level.getTargetLocationY()));
        formPanel.add(targetLocationYField);
    
        formPanel.add(new JLabel("Target Radius:"));
        targetRadiusField = new JTextField(String.valueOf(level.getTargetRadius()));
        formPanel.add(targetRadiusField);




      
        frame.add(formPanel, BorderLayout.CENTER);

        // Save button to update the level
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveChanges()); 
        buttonPanel.add(saveButton);
        


        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setSize(400, 400); 
        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }





    //save changes to the level
    private void saveChanges() {
        try {
            //here you get new values and save them 
            String newName = nameField.getText();
            String newHeightProfile = heightProfileField.getText();
            double newFrictionCoefficient = Double.parseDouble(frictionCoefficientField.getText());
            double newStartPositionX = Double.parseDouble(startPositionXField.getText());
            double newStartPositionY = Double.parseDouble(startPositionYField.getText());
            double newTargetLocationX = Double.parseDouble(targetLocationXField.getText());
            double newTargetLocationY = Double.parseDouble(targetLocationYField.getText());
            double newTargetRadius = Double.parseDouble(targetRadiusField.getText());
    
            level.setName(newName); 
            level.setHeightProfile(newHeightProfile);
            level.setFrictionCoefficient(newFrictionCoefficient);
            level.setStartPositionX(newStartPositionX);
            level.setStartPositionY(newStartPositionY);
            level.setTargetLocationX(newTargetLocationX);
            level.setTargetLocationY(newTargetLocationY);
            level.setTargetRadius(newTargetRadius);
    
            // Notify the listener
            if (editListener != null) {
                editListener.onLevelEdited(level);
            }
    
            frame.dispose(); 
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
//////////////////// CLASS //////////////////////////////////////////////
