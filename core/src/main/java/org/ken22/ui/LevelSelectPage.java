package org.ken22.ui;


//////////////////// IMPORTS //////////////////////////////////////////////
import org.ken22.interfaces.Level;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.ken22.ui.LevelEditor;
//////////////////// IMPORTS //////////////////////////////////////////////








//////////////////// CLASS //////////////////////////////////////////////
public class LevelSelectPage {

    //initializations
    private JFrame frame;
    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private List<Level> levels; 



    //listener stuff
    public interface LevelSelectListener {
        void onLevelSelected(Level level);
    }

    private LevelSelectListener selectListener;
    private LevelEditor.LevelEditListener editListener; 






    // Constructor
    public LevelSelectPage(List<Level> levels, LevelSelectListener listener) {
        this.selectListener = listener;
        this.levels = new ArrayList<>(levels); 
        frame = new JFrame("Level Selection");
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.LIGHT_GRAY);
        
        // refreshing sprite cranberry
        refreshLevels();

        scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        frame.add(scrollPane);
        frame.setSize(280, 600);
        frame.setLocationRelativeTo(null);
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }






    //////////////////// METHODS ////////////////////////////////////////////// 

    // refreshing the levels after modifications
    public void refreshLevels() {
        mainPanel.removeAll(); 

        for (Level level : levels) {
            JPanel levelPanel = createLevelPanel(level);
            mainPanel.add(levelPanel);
        }

        mainPanel.revalidate(); 
        mainPanel.repaint(); 
    }




    //listen
    public void setLevelEditListener(LevelEditor.LevelEditListener editListener) {
        this.editListener = editListener;
    }




    
    //creating them panels
    private JPanel createLevelPanel(Level level) {
        JPanel levelPanel = new JPanel();
        levelPanel.setLayout(new BoxLayout(levelPanel, BoxLayout.Y_AXIS));
        levelPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        levelPanel.setBackground(Color.WHITE);
        levelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(level.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        levelPanel.add(titleLabel);
        levelPanel.add(new JLabel("Height profile = " + level.getHeightProfile()));
        levelPanel.add(new JLabel("Friction coefficient = " + level.getFrictionCoefficient()));
        levelPanel.add(new JLabel("Start position X = " + level.getStartPositionX() + ", Y = " + level.getStartPositionY()));
        levelPanel.add(new JLabel("Target location X = " + level.getTargetLocationX() + ", Y = " + level.getTargetLocationY() + ", r = " + level.getTargetRadius()));

        JButton modifyButton = new JButton("Modify");
        modifyButton.setBackground(new Color(135, 206, 250));
        modifyButton.addActionListener(e -> new LevelEditor(level, editedLevel -> {
            int levelIndex = levels.indexOf(level);
            if (levelIndex != -1) {
                levels.set(levelIndex, editedLevel); 
            }
            refreshLevels(); 
        }));
        modifyButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        levelPanel.add(modifyButton);

        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(e -> {
            if (selectListener != null) {
                selectListener.onLevelSelected(level);
                frame.dispose(); 
            }
        });
        levelPanel.add(selectButton);

        levelPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        return levelPanel;
    }
     //////////////////// METHODS ////////////////////////////////////////////// 

}
//////////////////// CLASS //////////////////////////////////////////////
