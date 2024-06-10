package org.ken22.ui;

//////////////////// IMPORTS //////////////////////////////////////////////
import javax.swing.*;
import java.awt.*;
import org.ken22.controller.ApplicationController2;

//////////////////// IMPORTS //////////////////////////////////////////////







//////////////////// CLASS //////////////////////////////////////////////
public class InputPage2 implements LevelSelectPage.LevelSelectListener {



    //////////////////// INITIALIZATIONS //////////////////////////////////////////////
    private JFrame frame;
    private JPanel mainPanel;
    private JButton startButton;
    private JButton levelButton;
    private JButton phaseButton;
    private final ApplicationController2 app;
    private Level selectedLevel;
    //////////////////// INITIALIZATIONS //////////////////////////////////////////////







     ////////////////////  GUI CONSTRUCTOR //////////////////////////////////////////////
     public InputPage2(ApplicationController2 app) {
        this.app = app;


        //TO CHANGE BUTTONS, COSMETIC STUFF ETC
        frame = new JFrame("GOLF GAME");
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.PINK);

        Font buttonFont = new Font("Papyrus", Font.BOLD, 16); //hehe

        startButton = createButton("START   GAME", buttonFont);
        levelButton = createButton("LEVEL  SELECT", buttonFont);
        phaseButton = createButton("ODE   SOLVER", buttonFont);



        //BUTTONES
        startButton.addActionListener(e -> {
            if (selectedLevel != null) {
                new SetupMenu(selectedLevel);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a level first.", "No Level Selected", JOptionPane.WARNING_MESSAGE);
            }
        });
        phaseButton.addActionListener(e -> app.onPhaseOne());
        levelButton.addActionListener(e -> app.onLevelSelect(this));




      //adding stuff
        mainPanel.add(startButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(levelButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(phaseButton);

        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    ////////////////////  GUI CONSTRUCTOR //////////////////////////////////////////////









    //////////////////// METHODS //////////////////////////////////////////////
    // le button
    private JButton createButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 70));
        button.setFocusPainted(false);
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE); //
        return button;
    }

    //this will keep the selected level in memory and pass it on
    public void onLevelSelected(Level level) {
        this.selectedLevel = level; // Save the selected level
        levelButton.setText(level.getName());
    }
    public void updateSelectedLevel(Level level) {
        SwingUtilities.invokeLater(() -> onLevelSelected(level));
    }
//////////////////// METHODS //////////////////////////////////////////////







    //////////////////// MAIN //////////////////////////////////////////////
        public static void main(String[] args) {

            SwingUtilities.invokeLater(() -> {
                ApplicationController2 app = new ApplicationController2();
                InputPage2 InputPage2 = new InputPage2(app);
            });
        }
    //////////////////// MAIN //////////////////////////////////////////////

    }


    //////////////////// CLASS //////////////////////////////////////////////
